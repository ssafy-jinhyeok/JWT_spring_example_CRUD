package example.service;

import example.domain.Product;
import example.dto.ProductSearchCriteria;
import example.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 서비스
 * MyBatis 동적 SQL을 활용한 복잡한 검색 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    /**
     * 모든 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productMapper.findAll();
    }

    /**
     * ID로 상품 조회
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        log.debug("Fetching product by id: {}", id);
        return productMapper.findById(id);
    }

    /**
     * 상품 생성
     */
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());

        if (product.getStatus() == null) {
            product.setStatus("AVAILABLE");
        }

        productMapper.insert(product);
        log.info("Product created with id: {}", product.getId());

        return product;
    }

    /**
     * 상품 정보 수정
     */
    @Transactional
    public void updateProduct(Product product) {
        log.info("Updating product with id: {}", product.getId());

        Optional<Product> existing = productMapper.findById(product.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + product.getId());
        }

        productMapper.update(product);
        log.info("Product updated successfully");
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Optional<Product> existing = productMapper.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        productMapper.delete(id);
        log.info("Product deleted successfully");
    }

    /**
     * 복잡한 검색 조건으로 상품 조회
     * MyBatis 동적 SQL 활용
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(ProductSearchCriteria criteria) {
        log.debug("Searching products with criteria: {}", criteria);
        return productMapper.search(criteria);
    }

    /**
     * 카테고리별 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        return productMapper.findByCategory(category);
    }

    /**
     * 재고 업데이트
     * 주문 시 재고 감소, 반품 시 재고 증가 등에 사용
     */
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        log.info("Updating stock for product {}: {}", productId, quantity);

        Optional<Product> product = productMapper.findById(productId);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        // 재고 감소인 경우 재고 충분한지 확인
        if (quantity < 0 && product.get().getStockQuantity() + quantity < 0) {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }

        productMapper.updateStock(productId, quantity);
        log.info("Stock updated successfully");
    }

    /**
     * 재고가 부족한 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        log.debug("Fetching low stock products (threshold: {})", threshold);
        return productMapper.findLowStockProducts(threshold);
    }

    /**
     * 카테고리별 가격 일괄 조정
     * 예: 세일 이벤트로 특정 카테고리 상품 10% 할인
     */
    @Transactional
    public void adjustPricesByCategory(String category, BigDecimal priceMultiplier) {
        log.info("Adjusting prices for category {} by multiplier {}", category, priceMultiplier);

        if (priceMultiplier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price multiplier must be positive");
        }

        productMapper.updatePricesByCategory(category, priceMultiplier);
        log.info("Prices adjusted successfully");
    }
}
