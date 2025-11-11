package example.mapper;

import example.domain.Order;
import example.dto.OrderSearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 주문 Mapper 인터페이스
 * MyBatis의 Association과 Collection을 활용한 복잡한 조인 쿼리 구현
 */
@Mapper
public interface OrderMapper {

    /**
     * 모든 주문 조회 (사용자 정보 포함)
     * XML Mapper에서 구현 (Association 사용)
     */
    List<Order> findAll();

    /**
     * ID로 주문 조회 (사용자 정보 및 주문 상세 항목 포함)
     * XML Mapper에서 구현 (Association + Collection 사용)
     */
    Optional<Order> findById(Long id);

    /**
     * 사용자별 주문 조회
     * XML Mapper에서 구현
     */
    List<Order> findByUserId(Long userId);

    /**
     * 주문 생성
     * XML Mapper에서 구현
     */
    void insert(Order order);

    /**
     * 주문 상태 업데이트
     */
    @Update("UPDATE orders SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 주문 삭제
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    void delete(Long id);

    /**
     * 검색 조건에 따른 주문 조회
     * XML Mapper에서 구현 (복잡한 동적 SQL 사용)
     */
    List<Order> search(OrderSearchCriteria criteria);

    /**
     * 특정 기간의 주문 통계
     * XML Mapper에서 구현 (집계 함수 사용)
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_date BETWEEN #{startDate} AND #{endDate}")
    int countByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                         @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 사용자별 총 주문 금액
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = #{userId} AND status != 'CANCELLED'")
    java.math.BigDecimal getTotalAmountByUserId(Long userId);
}
