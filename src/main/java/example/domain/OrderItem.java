package example.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주문 상세 항목 도메인 모델
 */
@Schema(description = "주문 상세 항목")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Schema(description = "주문 상세 항목 고유 ID", example = "1")
    private Long id;

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;

    @Schema(description = "주문 당시 상품 가격", example = "1299.99")
    private BigDecimal price;

    @Schema(description = "소계 (수량 * 가격)", example = "2599.98")
    private BigDecimal subtotal;

    @Schema(description = "상품 정보 (Association)")
    private Product product;
}
