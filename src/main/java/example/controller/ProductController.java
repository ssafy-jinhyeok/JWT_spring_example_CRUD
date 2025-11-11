package example.controller;

import example.domain.Product;
import example.dto.ProductSearchCriteria;
import example.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 REST API Controller
 * MyBatis 동적 SQL을 활용한 복잡한 검색 API 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 모든 상품 조회
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * ID로 상품 조회
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product by id", id);
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 상품 생성
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        log.info("POST /api/products - Creating product: {}", product.getName());
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 상품 정보 수정
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        log.info("PUT /api/products/{} - Updating product", id);
        try {
            product.setId(id);
            productService.updateProduct(product);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to update product: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 상품 삭제
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete product: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 상품 검색 (복잡한 조건)
     * POST /api/products/search
     *
     * MyBatis 동적 SQL을 활용한 검색
     * - 상품명 부분 검색
     * - 카테고리 리스트 (IN 조건)
     * - 가격 범위
     * - 재고 여부
     * - 상태 리스트
     */
    @PostMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestBody ProductSearchCriteria criteria) {
        log.info("POST /api/products/search - Searching products with criteria: {}", criteria);
        List<Product> products = productService.searchProducts(criteria);
        return ResponseEntity.ok(products);
    }

    /**
     * 카테고리별 상품 조회
     * GET /api/products/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        log.info("GET /api/products/category/{} - Fetching products by category", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * 재고 업데이트
     * PATCH /api/products/{id}/stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("PATCH /api/products/{}/stock - Updating stock by: {}", id, quantity);
        try {
            productService.updateStock(id, quantity);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to update stock: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 재고가 부족한 상품 조회
     * GET /api/products/low-stock?threshold={threshold}
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        log.info("GET /api/products/low-stock?threshold={}", threshold);
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    /**
     * 카테고리별 가격 일괄 조정
     * PATCH /api/products/category/{category}/adjust-price
     *
     * 예: 세일 이벤트로 특정 카테고리 10% 할인
     * priceMultiplier=0.9 전달
     */
    @PatchMapping("/category/{category}/adjust-price")
    public ResponseEntity<Void> adjustPrices(
            @PathVariable String category,
            @RequestParam BigDecimal priceMultiplier) {
        log.info("PATCH /api/products/category/{}/adjust-price - Multiplier: {}", category, priceMultiplier);
        try {
            productService.adjustPricesByCategory(category, priceMultiplier);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to adjust prices: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
