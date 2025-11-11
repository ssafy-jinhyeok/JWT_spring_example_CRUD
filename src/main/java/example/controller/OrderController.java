package example.controller;

import example.domain.Order;
import example.dto.OrderSearchCriteria;
import example.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 주문 REST API Controller
 * MyBatis Association과 Collection을 활용한 복잡한 데이터 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 모든 주문 조회 (사용자 정보 포함)
     * GET /api/orders
     *
     * MyBatis Association으로 주문과 사용자 정보를 한 번에 조회
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET /api/orders - Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * ID로 주문 조회 (사용자 정보 및 주문 상세 항목 포함)
     * GET /api/orders/{id}
     *
     * MyBatis Association + Collection으로
     * 주문 -> 사용자, 주문 상세 항목 -> 상품을 모두 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("GET /api/orders/{} - Fetching order by id", id);
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자별 주문 조회
     * GET /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        log.info("GET /api/orders/user/{} - Fetching orders for user", userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 생성
     * POST /api/orders
     *
     * 복잡한 트랜잭션:
     * 1. 주문 생성
     * 2. 주문 상세 항목 생성 (배치 INSERT)
     * 3. 재고 감소
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        log.info("POST /api/orders - Creating order for user: {}", order.getUserId());
        try {
            Order created = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 주문 상태 업데이트
     * PATCH /api/orders/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("PATCH /api/orders/{}/status - Updating status to: {}", id, status);
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to update order status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 주문 취소
     * POST /api/orders/{id}/cancel
     *
     * 복잡한 트랜잭션:
     * 1. 주문 상태 변경
     * 2. 재고 복구
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        log.info("POST /api/orders/{}/cancel - Cancelling order", id);
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 주문 삭제
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /api/orders/{} - Deleting order", id);
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete order: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 주문 검색 (복잡한 조건)
     * POST /api/orders/search
     *
     * MyBatis 동적 SQL을 활용한 검색:
     * - 사용자 ID
     * - 주문 상태
     * - 날짜 범위
     * - 금액 범위
     * - 정렬 기준 및 방향
     */
    @PostMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(@RequestBody OrderSearchCriteria criteria) {
        log.info("POST /api/orders/search - Searching orders with criteria: {}", criteria);
        List<Order> orders = orderService.searchOrders(criteria);
        return ResponseEntity.ok(orders);
    }

    /**
     * 사용자별 총 주문 금액 조회
     * GET /api/orders/user/{userId}/total-amount
     */
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByUserId(@PathVariable Long userId) {
        log.info("GET /api/orders/user/{}/total-amount", userId);
        BigDecimal totalAmount = orderService.getTotalAmountByUserId(userId);
        return ResponseEntity.ok(totalAmount);
    }
}
