package example.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 도메인 모델
 * MyBatis의 자동 매핑 기능을 활용하여 데이터베이스와 매핑
 */
@Schema(description = "사용자 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Schema(description = "사용자 고유 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명 (로그인 ID)", example = "john_doe")
    private String username;

    @Schema(description = "비밀번호 (암호화된 값)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "이메일 주소", example = "john@example.com")
    private String email;

    @Schema(description = "사용자 전체 이름", example = "John Doe")
    private String fullName;

    @Schema(description = "계정 활성화 상태", example = "true")
    private Boolean active;

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private String role;

    @Schema(description = "생성 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2025-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
