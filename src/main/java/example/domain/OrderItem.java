package example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주문 상세 항목 도메인 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * 주문 상세 항목 고유 ID
     */
    private Long id;

    /**
     * 주문 ID (외래키)
     */
    private Long orderId;

    /**
     * 상품 ID (외래키)
     */
    private Long productId;

    /**
     * 주문 수량
     */
    private Integer quantity;

    /**
     * 주문 당시 상품 가격
     */
    private BigDecimal price;

    /**
     * 소계 (수량 * 가격)
     */
    private BigDecimal subtotal;

    /**
     * 상품 정보 (Association - N:1 관계)
     * MyBatis의 association 태그로 매핑
     */
    private Product product;
}
