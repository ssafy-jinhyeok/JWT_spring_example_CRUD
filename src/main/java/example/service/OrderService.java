package example.service;

import example.domain.Order;
import example.domain.OrderItem;
import example.domain.Product;
import example.dto.OrderSearchCriteria;
import example.mapper.OrderItemMapper;
import example.mapper.OrderMapper;
import example.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 주문 서비스
 * 복잡한 비즈니스 로직과 트랜잭션 관리
 * MyBatis의 Association과 Collection을 활용한 복잡한 데이터 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    /**
     * 모든 주문 조회 (사용자 정보 포함)
     * MyBatis Association으로 주문과 사용자 정보를 한 번에 조회
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.debug("Fetching all orders");
        return orderMapper.findAll();
    }

    /**
     * ID로 주문 조회 (사용자 정보 및 주문 상세 항목 포함)
     * MyBatis Association + Collection으로 복잡한 조인 쿼리 수행
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        log.debug("Fetching order by id: {}", id);
        return orderMapper.findById(id);
    }

    /**
     * 사용자별 주문 조회
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        log.debug("Fetching orders for user: {}", userId);
        return orderMapper.findByUserId(userId);
    }

    /**
     * 주문 생성
     * 복잡한 트랜잭션: 주문 생성 + 주문 상세 항목 생성 + 재고 감소
     */
    @Transactional
    public Order createOrder(Order order) {
        log.info("Creating new order for user: {}", order.getUserId());

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // 재고 확인 및 총액 계산
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            Optional<Product> product = productMapper.findById(item.getProductId());
            if (product.isEmpty()) {
                throw new IllegalArgumentException("Product not found: " + item.getProductId());
            }

            // 재고 확인
            if (product.get().getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                        product.get().getName(),
                        product.get().getStockQuantity(),
                        item.getQuantity())
                );
            }

            // 소계 계산 (주문 당시 가격 사용)
            BigDecimal price = product.get().getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setPrice(price);
            item.setSubtotal(subtotal);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        // 주문 생성
        orderMapper.insert(order);
        log.info("Order created with id: {}", order.getId());

        // 주문 상세 항목 생성 (배치 INSERT)
        order.getOrderItems().forEach(item -> item.setOrderId(order.getId()));
        orderItemMapper.insertBatch(order.getOrderItems());
        log.info("Order items created: {} items", order.getOrderItems().size());

        // 재고 감소
        for (OrderItem item : order.getOrderItems()) {
            productMapper.updateStock(item.getProductId(), -item.getQuantity());
        }
        log.info("Stock updated for all products");

        return order;
    }

    /**
     * 주문 상태 업데이트
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to: {}", orderId, status);

        Optional<Order> existing = orderMapper.findById(orderId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        orderMapper.updateStatus(orderId, status);
        log.info("Order status updated successfully");
    }

    /**
     * 주문 취소
     * 복잡한 트랜잭션: 주문 상태 변경 + 재고 복구
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);

        Optional<Order> order = orderMapper.findById(orderId);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        if ("CANCELLED".equals(order.get().getStatus())) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if ("DELIVERED".equals(order.get().getStatus())) {
            throw new IllegalStateException("Cannot cancel delivered order");
        }

        // 주문 상태 변경
        orderMapper.updateStatus(orderId, "CANCELLED");

        // 재고 복구
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        for (OrderItem item : items) {
            productMapper.updateStock(item.getProductId(), item.getQuantity());
        }

        log.info("Order cancelled and stock restored");
    }

    /**
     * 주문 삭제
     * 외래키 관계로 인해 주문 상세 항목을 먼저 삭제해야 함
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("Deleting order: {}", orderId);

        Optional<Order> existing = orderMapper.findById(orderId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        // 주문 상세 항목 먼저 삭제
        orderItemMapper.deleteByOrderId(orderId);

        // 주문 삭제
        orderMapper.delete(orderId);

        log.info("Order deleted successfully");
    }

    /**
     * 검색 조건으로 주문 조회
     * MyBatis 동적 SQL 활용
     */
    @Transactional(readOnly = true)
    public List<Order> searchOrders(OrderSearchCriteria criteria) {
        log.debug("Searching orders with criteria: {}", criteria);
        return orderMapper.search(criteria);
    }

    /**
     * 사용자별 총 주문 금액 조회
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUserId(Long userId) {
        log.debug("Calculating total amount for user: {}", userId);
        return orderMapper.getTotalAmountByUserId(userId);
    }
}
