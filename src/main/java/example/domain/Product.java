package example.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 도메인 모델
 */
@Schema(description = "상품 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Schema(description = "상품 고유 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "Laptop Pro 15")
    private String name;

    @Schema(description = "상품 설명", example = "고성능 노트북")
    private String description;

    @Schema(description = "상품 가격", example = "1299.99")
    private BigDecimal price;

    @Schema(description = "재고 수량", example = "50")
    private Integer stockQuantity;

    @Schema(description = "카테고리", example = "Electronics")
    private String category;

    @Schema(description = "판매 상태 (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)", example = "AVAILABLE")
    private String status;

    @Schema(description = "생성 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
