package example.mapper;

import example.domain.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 주문 상세 항목 Mapper 인터페이스
 */
@Mapper
public interface OrderItemMapper {

    /**
     * 주문 ID로 주문 상세 항목 조회 (상품 정보 포함)
     * XML Mapper에서 구현 (Association 사용)
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 주문 상세 항목 생성
     * XML Mapper에서 구현
     */
    void insert(OrderItem orderItem);

    /**
     * 주문 상세 항목 일괄 생성 (배치 insert)
     * XML Mapper에서 구현 (foreach 사용)
     */
    void insertBatch(List<OrderItem> orderItems);

    /**
     * 주문 상세 항목 수정
     */
    @Update("UPDATE order_items SET quantity = #{quantity}, subtotal = #{subtotal} WHERE id = #{id}")
    void update(OrderItem orderItem);

    /**
     * 주문 상세 항목 삭제
     */
    @Delete("DELETE FROM order_items WHERE id = #{id}")
    void delete(Long id);

    /**
     * 주문 ID로 주문 상세 항목 일괄 삭제
     */
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    void deleteByOrderId(Long orderId);

    /**
     * 특정 상품의 총 판매 수량
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM order_items WHERE product_id = #{productId}")
    int getTotalSoldQuantity(Long productId);
}
