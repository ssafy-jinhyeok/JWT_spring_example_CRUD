package example.service;

import example.domain.User;
import example.dto.AuthResponse;
import example.dto.LoginRequest;
import example.dto.SignupRequest;
import example.mapper.UserMapper;
import example.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직 서비스
 */
@Service
@Transactional
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserMapper userMapper,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider,
                      AuthenticationManager authenticationManager) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 회원가입
     */
    public AuthResponse signup(SignupRequest request) {
        // 사용자명 중복 확인
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 사용 중인 사용자명입니다: " + request.getUsername());
        }

        // 이메일 중복 확인
        if (userMapper.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 새 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .active(true)
                .role("ROLE_USER")
                .build();

        userMapper.insert(user);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    /**
     * 로그인
     */
    public AuthResponse login(LoginRequest request) {
        // 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // 사용자 정보 조회
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    /**
     * 회원탈퇴
     */
    public void deleteAccount(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        userMapper.delete(user.getId());
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }
}
