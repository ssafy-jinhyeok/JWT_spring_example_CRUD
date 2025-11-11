package example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 도메인 모델
 * MyBatis의 자동 매핑 기능을 활용하여 데이터베이스와 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 사용자 고유 ID
     */
    private Long id;

    /**
     * 사용자명 (로그인 ID)
     */
    private String username;

    /**
     * 비밀번호 (암호화된 값)
     */
    private String password;

    /**
     * 이메일 주소
     */
    private String email;

    /**
     * 사용자 전체 이름
     */
    private String fullName;

    /**
     * 계정 활성화 상태
     */
    private Boolean active;

    /**
     * 생성 일시
     * MyBatis의 map-underscore-to-camel-case 설정으로 created_at 컬럼과 자동 매핑
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}
