package example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 검색 조건 DTO
 * MyBatis 동적 SQL의 foreach, if 등에서 사용
 */
@Schema(description = "상품 검색 조건")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {

    @Schema(description = "상품명 (부분 검색)", example = "Laptop")
    private String name;

    @Schema(description = "카테고리 리스트 (IN 조건)", example = "[\"Electronics\", \"Computers\"]")
    private List<String> categories;

    @Schema(description = "최소 가격", example = "100.00")
    private BigDecimal minPrice;

    @Schema(description = "최대 가격", example = "2000.00")
    private BigDecimal maxPrice;

    @Schema(description = "재고 있는 상품만 조회 여부", example = "true")
    private Boolean inStockOnly;

    @Schema(description = "상태 리스트 (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)",
        example = "[\"AVAILABLE\"]")
    private List<String> statuses;
}
