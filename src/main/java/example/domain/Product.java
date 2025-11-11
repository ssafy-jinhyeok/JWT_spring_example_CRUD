package example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 도메인 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * 상품 고유 ID
     */
    private Long id;

    /**
     * 상품명
     */
    private String name;

    /**
     * 상품 설명
     */
    private String description;

    /**
     * 상품 가격
     */
    private BigDecimal price;

    /**
     * 재고 수량
     */
    private Integer stockQuantity;

    /**
     * 카테고리
     */
    private String category;

    /**
     * 판매 상태 (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)
     */
    private String status;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}
