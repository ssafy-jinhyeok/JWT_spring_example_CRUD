package example.controller;

import example.dto.AuthResponse;
import example.dto.LoginRequest;
import example.dto.MessageResponse;
import example.dto.SignupRequest;
import example.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 */
@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃 등 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록하고 JWT 토큰을 발급합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 사용자명/이메일 등)")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Parameter(description = "회원가입 요청 정보") @Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 사용자명 또는 비밀번호)")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "로그인 요청 정보") @Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @Operation(summary = "로그아웃", description = "현재 세션을 종료합니다. JWT는 stateless이므로 클라이언트에서 토큰을 삭제해야 합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("로그아웃되었습니다"));
    }

    @Operation(summary = "회원탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원탈퇴 성공",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "회원탈퇴 처리 중 오류 발생")
    })
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            authService.deleteAccount(username);
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(new MessageResponse("회원탈퇴가 완료되었습니다"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("회원탈퇴 처리 중 오류가 발생했습니다"));
        }
    }

    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 인증된 현재 사용자의 정보를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            return ResponseEntity.ok(authService.getCurrentUser());
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("인증되지 않은 사용자입니다"));
        }
    }
}
