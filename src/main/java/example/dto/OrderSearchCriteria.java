package example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 검색 조건 DTO
 * MyBatis 동적 SQL에서 사용
 */
@Schema(description = "주문 검색 조건")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCriteria {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)", example = "PENDING")
    private String status;

    @Schema(description = "검색 시작 일시", example = "2025-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "검색 종료 일시", example = "2025-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "최소 주문 금액", example = "100.00")
    private java.math.BigDecimal minAmount;

    @Schema(description = "최대 주문 금액", example = "10000.00")
    private java.math.BigDecimal maxAmount;

    @Schema(description = "정렬 기준 (order_date, total_amount)", example = "order_date")
    private String sortBy;

    @Schema(description = "정렬 방향 (ASC, DESC)", example = "DESC")
    private String sortDirection;
}
