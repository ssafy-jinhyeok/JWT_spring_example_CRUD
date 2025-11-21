package example.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 도메인 모델
 * MyBatis의 Association과 Collection을 활용한 복잡한 매핑 예시
 */
@Schema(description = "주문 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Schema(description = "주문 고유 ID", example = "1")
    private Long id;

    @Schema(description = "주문한 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)", example = "PENDING")
    private String status;

    @Schema(description = "총 주문 금액", example = "1345.97")
    private BigDecimal totalAmount;

    @Schema(description = "배송 주소", example = "123 Main St, New York, NY 10001")
    private String shippingAddress;

    @Schema(description = "주문 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime orderDate;

    @Schema(description = "수정 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "주문한 사용자 정보 (Association)")
    private User user;

    @Schema(description = "주문 상세 항목 목록 (Collection)")
    private List<OrderItem> orderItems;
}
