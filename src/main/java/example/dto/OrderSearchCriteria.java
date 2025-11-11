package example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 검색 조건 DTO
 * MyBatis 동적 SQL에서 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCriteria {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 주문 상태
     */
    private String status;

    /**
     * 검색 시작 일자
     */
    private LocalDateTime startDate;

    /**
     * 검색 종료 일자
     */
    private LocalDateTime endDate;

    /**
     * 최소 금액
     */
    private java.math.BigDecimal minAmount;

    /**
     * 최대 금액
     */
    private java.math.BigDecimal maxAmount;

    /**
     * 정렬 기준 (order_date, total_amount 등)
     */
    private String sortBy;

    /**
     * 정렬 방향 (ASC, DESC)
     */
    private String sortDirection;
}
