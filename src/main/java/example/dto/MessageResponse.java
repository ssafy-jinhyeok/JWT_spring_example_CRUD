package example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일반 메시지 응답 DTO
 */
@Schema(description = "일반 메시지 응답")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    @Schema(description = "응답 메시지", example = "작업이 성공적으로 완료되었습니다")
    private String message;
}
