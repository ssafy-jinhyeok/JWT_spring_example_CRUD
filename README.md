# MyBatis Advanced Example - Spring Boot CRUD Application

이 프로젝트는 MyBatis의 고급 기능을 활용한 Spring Boot 애플리케이션 예시입니다.

## 주요 특징

### MyBatis 고급 기능 구현

#### 1. ResultMap (복잡한 매핑)
- Association/Collection이 필요한 복잡한 관계 매핑에 사용
- `autoMapping="true"`로 단순 컬럼은 자동 매핑
- `OrderMapper.xml`, `OrderItemMapper.xml` 참조

#### 2. Association (N:1 관계)
- 주문(Order) -> 사용자(User) 매핑
- 주문 상세 항목(OrderItem) -> 상품(Product) 매핑
- `OrderMapper.xml` 참조

#### 3. Collection (1:N 관계)
- 주문(Order) -> 주문 상세 항목들(OrderItems) 매핑
- 중첩된 ResultMap으로 복잡한 조인 처리
- `OrderMapper.xml`의 `orderDetailResultMap` 참조

#### 4. 동적 SQL
- **`<if>`**: 조건부 쿼리 생성
- **`<where>`**: WHERE 절 자동 생성 및 AND/OR 처리
- **`<set>`**: UPDATE SET 절 자동 생성
- **`<choose>`, `<when>`, `<otherwise>`**: switch-case 같은 조건문
- **`<foreach>`**: 컬렉션 반복 (IN 절, 배치 INSERT 등)
- `ProductMapper.xml`의 `search`, `OrderMapper.xml`의 `search` 참조

#### 5. 배치 작업
- 배치 INSERT: 여러 주문 상세 항목을 한 번에 삽입
- `OrderItemMapper.xml`의 `insertBatch` 참조

#### 6. 트랜잭션 관리
- `@Transactional`을 사용한 선언적 트랜잭션
- 복잡한 비즈니스 로직에서 여러 테이블 동시 업데이트
- `OrderService.createOrder()` 참조

## 프로젝트 구조

```
src/main/java/example/
├── config/
│   └── MyBatisConfig.java          # MyBatis 설정
├── controller/
│   ├── UserController.java         # 사용자 REST API
│   ├── ProductController.java      # 상품 REST API
│   └── OrderController.java        # 주문 REST API
├── domain/
│   ├── User.java                   # 사용자 도메인
│   ├── Product.java                # 상품 도메인
│   ├── Order.java                  # 주문 도메인
│   └── OrderItem.java              # 주문 상세 항목 도메인
├── dto/
│   ├── ProductSearchCriteria.java  # 상품 검색 조건 DTO
│   └── OrderSearchCriteria.java    # 주문 검색 조건 DTO
├── mapper/
│   ├── UserMapper.java             # 사용자 Mapper 인터페이스
│   ├── ProductMapper.java          # 상품 Mapper 인터페이스
│   ├── OrderMapper.java            # 주문 Mapper 인터페이스
│   └── OrderItemMapper.java        # 주문 상세 항목 Mapper 인터페이스
└── service/
    ├── UserService.java            # 사용자 서비스
    ├── ProductService.java         # 상품 서비스
    └── OrderService.java           # 주문 서비스

src/main/resources/
├── mapper/
│   ├── UserMapper.xml              # 사용자 Mapper XML
│   ├── ProductMapper.xml           # 상품 Mapper XML (동적 SQL)
│   ├── OrderMapper.xml             # 주문 Mapper XML (Association + Collection)
│   └── OrderItemMapper.xml         # 주문 상세 항목 Mapper XML (배치 INSERT)
├── schema.sql                      # 데이터베이스 스키마
├── data.sql                        # 초기 데이터
└── application.yml                 # 설정 파일
```

## 실행 방법

### 1. 빌드
```bash
./gradlew build
```

### 2. 실행
```bash
./gradlew bootRun
```

### 3. H2 콘솔 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비어있음)

## API 엔드포인트

### 인증 API
- `POST /api/auth/signup` - 회원가입 (JWT 토큰 발급)
- `POST /api/auth/login` - 로그인 (JWT 토큰 발급)
- `POST /api/auth/logout` - 로그아웃
- `DELETE /api/auth/account` - 회원탈퇴
- `GET /api/auth/me` - 현재 사용자 정보 조회

### 사용자 API
- `GET /api/users` - 모든 사용자 조회
- `GET /api/users/{id}` - ID로 사용자 조회
- `GET /api/users/username/{username}` - 사용자명으로 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{id}` - 사용자 수정
- `DELETE /api/users/{id}` - 사용자 삭제
- `PATCH /api/users/{id}/active` - 계정 활성화/비활성화
- `GET /api/users/stats/active-count` - 활성 사용자 수

### 상품 API
- `GET /api/products` - 모든 상품 조회
- `GET /api/products/{id}` - ID로 상품 조회
- `POST /api/products` - 상품 생성
- `PUT /api/products/{id}` - 상품 수정
- `DELETE /api/products/{id}` - 상품 삭제
- `POST /api/products/search` - 상품 검색 (동적 SQL)
- `GET /api/products/category/{category}` - 카테고리별 조회
- `PATCH /api/products/{id}/stock` - 재고 업데이트
- `GET /api/products/low-stock` - 재고 부족 상품 조회
- `PATCH /api/products/category/{category}/adjust-price` - 카테고리별 가격 조정

### 주문 API
- `GET /api/orders` - 모든 주문 조회 (사용자 정보 포함)
- `GET /api/orders/{id}` - ID로 주문 조회 (사용자 + 주문 상세 + 상품 정보 모두 포함)
- `GET /api/orders/user/{userId}` - 사용자별 주문 조회
- `POST /api/orders` - 주문 생성 (재고 감소 포함)
- `PATCH /api/orders/{id}/status` - 주문 상태 변경
- `POST /api/orders/{id}/cancel` - 주문 취소 (재고 복구 포함)
- `DELETE /api/orders/{id}` - 주문 삭제
- `POST /api/orders/search` - 주문 검색 (동적 SQL)
- `GET /api/orders/user/{userId}/total-amount` - 사용자별 총 주문 금액

## 사용 예시

### 1. 회원가입
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "fullName": "New User"
  }'
```

응답 예시:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 4,
    "username": "newuser",
    "email": "newuser@example.com",
    "fullName": "New User",
    "active": true,
    "role": "ROLE_USER",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
}
```

### 2. 로그인
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

응답 예시:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "active": true,
    "role": "ROLE_USER",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
}
```

### 3. 상품 검색 (동적 SQL)
```bash
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "categories": ["Electronics"],
    "minPrice": 100,
    "maxPrice": 2000,
    "inStockOnly": true
  }'
```

### 4. 주문 생성
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "shippingAddress": "123 Main St",
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

### 5. 주문 상세 조회 (Association + Collection)
```bash
curl http://localhost:8080/api/orders/1
```

응답 예시:
```json
{
  "id": 1,
  "userId": 1,
  "status": "DELIVERED",
  "totalAmount": 1345.97,
  "shippingAddress": "123 Main St, New York, NY 10001",
  "orderDate": "2025-01-15T10:30:00",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe"
  },
  "orderItems": [
    {
      "id": 1,
      "quantity": 1,
      "price": 1299.99,
      "subtotal": 1299.99,
      "product": {
        "id": 1,
        "name": "Laptop Pro 15",
        "price": 1299.99,
        "category": "Electronics"
      }
    }
  ]
}
```

## 학습 포인트

### 1. ResultMap과 자동 매핑
- `map-underscore-to-camel-case: true` 설정으로 underscore → camelCase 자동 변환
- 단순 테이블 매핑은 `resultType` 사용, 복잡한 관계는 `resultMap` + `autoMapping="true"` 사용
- `id` 태그로 Primary Key 지정 시 성능 향상

### 2. Association vs Collection
- **Association**: N:1 관계 (주문 -> 사용자)
- **Collection**: 1:N 관계 (주문 -> 주문 상세 항목들)
- 중첩 가능: Collection 내부에 Association 사용 가능

### 3. 동적 SQL의 활용
- `<if>`: null 체크 및 조건부 쿼리
- `<where>`: 자동으로 WHERE 추가 및 AND/OR 처리
- `<set>`: UPDATE 시 콤마 처리 자동화
- `<foreach>`: IN 절, 배치 INSERT에 유용

### 4. 트랜잭션 관리
- `@Transactional`로 선언적 트랜잭션
- `readOnly=true`로 읽기 전용 트랜잭션 최적화
- 복잡한 비즈니스 로직에서 데이터 일관성 보장

### 5. 성능 최적화
- 배치 INSERT로 여러 행을 한 번에 삽입
- 지연 로딩으로 불필요한 데이터 조회 방지
- 인덱스 활용으로 조회 성능 향상

## 참고 자료

- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)
- [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
