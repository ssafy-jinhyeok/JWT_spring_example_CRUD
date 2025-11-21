package example.controller;

import example.domain.Product;
import example.dto.ProductSearchCriteria;
import example.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "상품", description = "상품 CRUD 및 검색 API")
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "모든 상품 조회", description = "등록된 모든 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "ID로 상품 조회", description = "상품 ID로 특정 상품을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product by id", id);
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "상품 생성", description = "새로운 상품을 등록합니다")
    @ApiResponse(responseCode = "201", description = "생성 성공",
        content = @Content(schema = @Schema(implementation = Product.class)))
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "생성할 상품 정보") @Valid @RequestBody Product product) {
        log.info("POST /api/products - Creating product: {}", product.getName());
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "상품 정보 수정", description = "기존 상품의 정보를 수정합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "수정할 상품 정보") @Valid @RequestBody Product product) {
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

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "삭제할 상품 ID", example = "1") @PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete product: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "상품 검색", description = "다양한 조건으로 상품을 검색합니다 (MyBatis 동적 SQL 활용)")
    @ApiResponse(responseCode = "200", description = "검색 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    @PostMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @Parameter(description = "검색 조건 (상품명, 카테고리, 가격 범위, 재고 여부, 상태)")
            @RequestBody ProductSearchCriteria criteria) {
        log.info("POST /api/products/search - Searching products with criteria: {}", criteria);
        List<Product> products = productService.searchProducts(criteria);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리에 속한 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "카테고리명", example = "Electronics") @PathVariable String category) {
        log.info("GET /api/products/category/{} - Fetching products by category", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "재고 업데이트", description = "상품의 재고 수량을 변경합니다 (양수: 증가, 음수: 감소)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "재고 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (재고 부족 등)")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "변경할 수량 (양수: 증가, 음수: 감소)", example = "10") @RequestParam Integer quantity) {
        log.info("PATCH /api/products/{}/stock - Updating stock by: {}", id, quantity);
        try {
            productService.updateStock(id, quantity);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed to update stock: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "재고 부족 상품 조회", description = "재고가 지정된 임계값 이하인 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "재고 임계값 (기본값: 10)", example = "10")
            @RequestParam(defaultValue = "10") Integer threshold) {
        log.info("GET /api/products/low-stock?threshold={}", threshold);
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "카테고리별 가격 일괄 조정",
        description = "특정 카테고리의 모든 상품 가격을 일괄 조정합니다. 예: 10% 할인 시 priceMultiplier=0.9")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "가격 조정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/category/{category}/adjust-price")
    public ResponseEntity<Void> adjustPrices(
            @Parameter(description = "카테고리명", example = "Electronics") @PathVariable String category,
            @Parameter(description = "가격 배수 (0.9 = 10% 할인, 1.1 = 10% 인상)", example = "0.9")
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
