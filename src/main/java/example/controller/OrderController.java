package example.controller;

import example.domain.Order;
import example.dto.OrderSearchCriteria;
import example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "주문", description = "주문 CRUD 및 검색 API (MyBatis Association/Collection 활용)")
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "모든 주문 조회", description = "모든 주문 목록을 사용자 정보와 함께 조회합니다 (MyBatis Association 활용)")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class))))
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET /api/orders - Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "ID로 주문 상세 조회",
        description = "주문 ID로 주문 상세 정보를 조회합니다. 사용자 정보, 주문 상세 항목, 상품 정보를 모두 포함합니다 (MyBatis Association + Collection)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "주문 ID", example = "1") @PathVariable Long id) {
        log.info("GET /api/orders/{} - Fetching order by id", id);
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용자별 주문 조회", description = "특정 사용자의 모든 주문 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class))))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {
        log.info("GET /api/orders/user/{} - Fetching orders for user", userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 생성",
        description = "새로운 주문을 생성합니다. 주문 생성, 주문 상세 항목 배치 INSERT, 재고 감소가 트랜잭션으로 처리됩니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "주문 생성 성공",
            content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (재고 부족, 존재하지 않는 상품 등)")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "주문 정보 (userId, shippingAddress, orderItems 필수)")
            @Valid @RequestBody Order order) {
        log.info("POST /api/orders - Creating order for user: {}", order.getUserId());
        try {
            Order created = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "주문 상태 업데이트", description = "주문의 상태를 변경합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "주문 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "변경할 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)",
                example = "SHIPPED") @RequestParam String status) {
        log.info("PATCH /api/orders/{}/status - Updating status to: {}", id, status);
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to update order status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "주문 취소",
        description = "주문을 취소합니다. 주문 상태 변경과 재고 복구가 트랜잭션으로 처리됩니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
        @ApiResponse(responseCode = "400", description = "취소 불가 (이미 취소됨, 배송 완료 등)")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "취소할 주문 ID", example = "1") @PathVariable Long id) {
        log.info("POST /api/orders/{}/cancel - Cancelling order", id);
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "삭제할 주문 ID", example = "1") @PathVariable Long id) {
        log.info("DELETE /api/orders/{} - Deleting order", id);
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete order: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "주문 검색",
        description = "다양한 조건으로 주문을 검색합니다 (MyBatis 동적 SQL 활용). 사용자 ID, 주문 상태, 날짜 범위, 금액 범위, 정렬 기준 지원")
    @ApiResponse(responseCode = "200", description = "검색 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class))))
    @PostMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(
            @Parameter(description = "검색 조건") @RequestBody OrderSearchCriteria criteria) {
        log.info("POST /api/orders/search - Searching orders with criteria: {}", criteria);
        List<Order> orders = orderService.searchOrders(criteria);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "사용자별 총 주문 금액 조회", description = "특정 사용자의 총 주문 금액을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = BigDecimal.class, example = "1500.00")))
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByUserId(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {
        log.info("GET /api/orders/user/{}/total-amount", userId);
        BigDecimal totalAmount = orderService.getTotalAmountByUserId(userId);
        return ResponseEntity.ok(totalAmount);
    }
}
