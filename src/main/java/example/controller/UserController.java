package example.controller;

import example.domain.User;
import example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 사용자 REST API Controller
 * CRUD 작업을 위한 RESTful API 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 모든 사용자 조회
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * ID로 사용자 조회
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user by id", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자명으로 조회
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/users/username/{} - Fetching user by username", username);
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자 생성
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("POST /api/users - Creating user: {}", user.getUsername());
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 사용자 정보 수정
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("PUT /api/users/{} - Updating user", id);
        try {
            user.setId(id);
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to update user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 사용자 삭제
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 계정 활성화/비활성화
     * PATCH /api/users/{id}/active
     */
    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> updateActiveStatus(@PathVariable Long id, @RequestParam Boolean active) {
        log.info("PATCH /api/users/{}/active - Setting active status to: {}", id, active);
        try {
            userService.updateActiveStatus(id, active);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update active status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 활성 사용자 수 조회
     * GET /api/users/stats/active-count
     */
    @GetMapping("/stats/active-count")
    public ResponseEntity<Integer> getActiveUserCount() {
        log.info("GET /api/users/stats/active-count");
        int count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
}
