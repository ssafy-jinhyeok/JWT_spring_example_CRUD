package example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 도메인 모델
 * MyBatis의 Association과 Collection을 활용한 복잡한 매핑 예시
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 주문 고유 ID
     */
    private Long id;

    /**
     * 주문한 사용자 ID (외래키)
     */
    private Long userId;

    /**
     * 주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
     */
    private String status;

    /**
     * 총 금액
     */
    private BigDecimal totalAmount;

    /**
     * 배송 주소
     */
    private String shippingAddress;

    /**
     * 주문 일시
     */
    private LocalDateTime orderDate;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    /**
     * 주문한 사용자 정보 (Association - N:1 관계)
     * MyBatis의 association 태그로 매핑
     */
    private User user;

    /**
     * 주문 상세 항목 리스트 (Collection - 1:N 관계)
     * MyBatis의 collection 태그로 매핑
     */
    private List<OrderItem> orderItems;
}
