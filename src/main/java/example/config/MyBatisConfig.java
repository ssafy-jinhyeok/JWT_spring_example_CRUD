package example.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis 설정 클래스
 *
 * 주요 기능:
 * 1. Mapper 스캔: @MapperScan으로 Mapper 인터페이스 자동 등록
 * 2. SqlSessionFactory 설정: MyBatis의 핵심 객체 생성
 * 3. 트랜잭션 관리자 설정: @Transactional 지원
 *
 * 참고: application.yml의 mybatis 설정과 함께 작동
 */
@Configuration
@EnableTransactionManagement  // @Transactional 어노테이션 활성화
@MapperScan("example.mapper")  // Mapper 인터페이스 스캔 패키지
public class MyBatisConfig {

    /**
     * SqlSessionFactory Bean 설정
     *
     * MyBatis의 핵심 객체로, 데이터베이스 연결 및 SQL 실행을 담당
     * application.yml의 mybatis 설정이 자동으로 적용되지만,
     * 추가적인 커스터마이징이 필요한 경우 여기서 설정 가능
     *
     * @param dataSource Spring Boot가 자동 생성한 DataSource
     * @return SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // Mapper XML 파일 위치 설정
        // application.yml의 mybatis.mapper-locations 설정과 동일
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml")
        );

        // 타입 별칭 패키지 설정
        // application.yml의 mybatis.type-aliases-package 설정과 동일
        sessionFactory.setTypeAliasesPackage("example.domain");

        /*
         * 추가 설정 예시 (필요시 주석 해제):
         *
         * // Configuration 객체를 통한 세부 설정
         * org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
         * configuration.setMapUnderscoreToCamelCase(true);  // 언더스코어를 카멜케이스로
         * configuration.setLazyLoadingEnabled(true);         // 지연 로딩 활성화
         * sessionFactory.setConfiguration(configuration);
         *
         * // TypeHandler 등록 (커스텀 타입 변환)
         * sessionFactory.setTypeHandlers(new TypeHandler[]{new CustomTypeHandler()});
         *
         * // Interceptor 등록 (SQL 실행 전후 처리)
         * sessionFactory.setPlugins(new Interceptor[]{new CustomInterceptor()});
         */

        return sessionFactory.getObject();
    }

    /**
     * 트랜잭션 관리자 설정
     *
     * @Transactional 어노테이션을 사용한 선언적 트랜잭션 관리를 위해 필요
     * DataSourceTransactionManager: JDBC 기반 트랜잭션 관리
     *
     * @param dataSource Spring Boot가 자동 생성한 DataSource
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /*
     * 고급 설정 예시:
     *
     * 1. 페이징 플러그인 (PageHelper)
     * @Bean
     * public PageInterceptor pageInterceptor() {
     *     PageInterceptor pageInterceptor = new PageInterceptor();
     *     Properties properties = new Properties();
     *     properties.setProperty("helperDialect", "h2");
     *     pageInterceptor.setProperties(properties);
     *     return pageInterceptor;
     * }
     *
     * 2. 커스텀 TypeHandler (Enum, JSON 등 특수 타입 처리)
     * @Bean
     * public TypeHandler<?> customTypeHandler() {
     *     return new CustomTypeHandler();
     * }
     *
     * 3. SqlSessionTemplate (멀티 스레드 환경에서 안전한 SqlSession)
     * @Bean
     * public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
     *     return new SqlSessionTemplate(sqlSessionFactory);
     * }
     */
}
