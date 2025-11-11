package example.service;

import example.domain.User;
import example.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 서비스
 * @Transactional을 사용한 트랜잭션 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userMapper.findAll();
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userMapper.findById(id);
    }

    /**
     * 사용자명으로 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userMapper.findByUsername(username);
    }

    /**
     * 사용자 생성
     * useGeneratedKeys로 자동 생성된 ID가 user 객체에 설정됨
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());

        // 중복 체크
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        user.setActive(true);
        userMapper.insert(user);
        log.info("User created with id: {}", user.getId());

        return user;
    }

    /**
     * 사용자 정보 수정
     * MyBatis 동적 SQL로 null이 아닌 필드만 업데이트
     */
    @Transactional
    public void updateUser(User user) {
        log.info("Updating user with id: {}", user.getId());

        Optional<User> existing = userMapper.findById(user.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }

        userMapper.update(user);
        log.info("User updated successfully");
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        Optional<User> existing = userMapper.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        userMapper.delete(id);
        log.info("User deleted successfully");
    }

    /**
     * 계정 활성화/비활성화
     */
    @Transactional
    public void updateActiveStatus(Long id, Boolean active) {
        log.info("Updating active status for user {}: {}", id, active);
        userMapper.updateActiveStatus(id, active);
    }

    /**
     * 활성 사용자 수 조회
     */
    @Transactional(readOnly = true)
    public int countActiveUsers() {
        return userMapper.countActiveUsers();
    }
}
