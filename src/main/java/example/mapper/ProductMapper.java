package example.mapper;

import example.domain.Product;
import example.dto.ProductSearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 상품 Mapper 인터페이스
 * 동적 SQL을 활용한 복잡한 검색 쿼리 구현
 */
@Mapper
public interface ProductMapper {

    /**
     * 모든 상품 조회
     * XML Mapper에서 구현
     */
    List<Product> findAll();

    /**
     * ID로 상품 조회
     */
    Optional<Product> findById(Long id);

    /**
     * 상품 생성
     * XML Mapper에서 구현
     */
    void insert(Product product);

    /**
     * 상품 정보 수정
     * XML Mapper에서 구현 (동적 SQL 사용)
     */
    void update(Product product);

    /**
     * 상품 삭제
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);

    /**
     * 검색 조건에 따른 상품 조회
     * XML Mapper에서 구현 (if, foreach 등 동적 SQL 사용)
     */
    List<Product> search(ProductSearchCriteria criteria);

    /**
     * 카테고리별 상품 조회
     */
    @Select("SELECT * FROM products WHERE category = #{category} AND status = 'AVAILABLE' ORDER BY created_at DESC")
    List<Product> findByCategory(String category);

    /**
     * 재고 업데이트
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity + #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 재고가 부족한 상품 조회
     */
    @Select("SELECT * FROM products WHERE stock_quantity < #{threshold} ORDER BY stock_quantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * 상품 가격 일괄 업데이트 (배치 작업 예시)
     * XML Mapper에서 구현
     */
    void updatePricesByCategory(@Param("category") String category, @Param("priceMultiplier") java.math.BigDecimal priceMultiplier);
}
