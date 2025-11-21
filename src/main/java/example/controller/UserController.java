package example.controller;

import example.domain.User;
import example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자", description = "사용자 CRUD API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "ID로 사용자 조회", description = "사용자 ID로 특정 사용자를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user by id", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용자명으로 조회", description = "사용자명으로 특정 사용자를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "사용자명", example = "john_doe") @PathVariable String username) {
        log.info("GET /api/users/username/{} - Fetching user by username", username);
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 사용자명/이메일 등)")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "생성할 사용자 정보") @Valid @RequestBody User user) {
        log.info("POST /api/users - Creating user: {}", user.getUsername());
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "수정할 사용자 정보") @Valid @RequestBody User user) {
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

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 사용자 ID", example = "1") @PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "계정 활성화/비활성화", description = "사용자 계정의 활성화 상태를 변경합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "400", description = "상태 변경 실패")
    })
    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> updateActiveStatus(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "활성화 여부", example = "true") @RequestParam Boolean active) {
        log.info("PATCH /api/users/{}/active - Setting active status to: {}", id, active);
        try {
            userService.updateActiveStatus(id, active);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update active status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "활성 사용자 수 조회", description = "현재 활성화된 사용자의 수를 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = Integer.class, example = "42")))
    @GetMapping("/stats/active-count")
    public ResponseEntity<Integer> getActiveUserCount() {
        log.info("GET /api/users/stats/active-count");
        int count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
}
