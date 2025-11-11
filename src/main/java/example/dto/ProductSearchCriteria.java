package example.dto;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {

    /**
     * 상품명 (부분 검색)
     */
    private String name;

    /**
     * 카테고리 리스트 (IN 조건)
     */
    private List<String> categories;

    /**
     * 최소 가격
     */
    private BigDecimal minPrice;

    /**
     * 최대 가격
     */
    private BigDecimal maxPrice;

    /**
     * 재고 있는 상품만 조회 여부
     */
    private Boolean inStockOnly;

    /**
     * 상태 리스트
     */
    private List<String> statuses;
}
