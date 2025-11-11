package example.controller;

import example.dto.AuthResponse;
import example.dto.LoginRequest;
import example.dto.MessageResponse;
import example.dto.SignupRequest;
import example.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 회원가입
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 로그인
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * 로그아웃
     * POST /api/auth/logout
     *
     * JWT는 stateless이므로 서버에서 별도 처리 없이
     * 클라이언트에서 토큰을 삭제하면 됨
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("로그아웃되었습니다"));
    }

    /**
     * 회원탈퇴
     * DELETE /api/auth/account
     */
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

    /**
     * 현재 사용자 정보 조회
     * GET /api/auth/me
     */
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
