package example.mapper;

import example.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Mapper 인터페이스
 * MyBatis의 어노테이션 기반 매핑과 XML 기반 매핑을 혼합 사용
 *
 * @Mapper 어노테이션으로 Spring이 자동으로 빈으로 등록
 */
@Mapper
public interface UserMapper {

    /**
     * 모든 사용자 조회
     * XML Mapper에서 구현
     */
    List<User> findAll();

    /**
     * ID로 사용자 조회
     * XML Mapper에서 구현 (ResultMap 사용 예시)
     */
    Optional<User> findById(Long id);

    /**
     * 사용자명으로 조회
     * 어노테이션 기반 매핑 예시
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 조회
     * 어노테이션 기반 매핑 예시
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);

    /**
     * 사용자 생성
     * XML Mapper에서 구현 (useGeneratedKeys 사용)
     */
    void insert(User user);

    /**
     * 사용자 정보 수정
     * XML Mapper에서 구현 (동적 SQL 사용)
     */
    void update(User user);

    /**
     * 사용자 삭제
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);

    /**
     * 활성 사용자 수 조회
     * 어노테이션 기반 매핑 예시
     */
    @Select("SELECT COUNT(*) FROM users WHERE active = true")
    int countActiveUsers();

    /**
     * 사용자명 존재 여부 확인
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);

    /**
     * 계정 활성화 상태 변경
     */
    @Update("UPDATE users SET active = #{active}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);
}
