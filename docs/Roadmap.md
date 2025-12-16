## 로드맵

### ✅ 1. 핵심 기능 구현 (완료)
**구현 완료**
- ✅ `AuctionController`, `AuctionService`, `AuctionRepository`
- ✅ 경매 생성, 조회, 수정, 삭제 (CRUD)
- ✅ 입찰 처리
- ✅ 즉시구매 로직
- ✅ 경매 종료 처리
- ✅ WebSocket + Redis Pub/Sub 기반 실시간 입찰 기능

---

### ✅ 2. 사용자 관리 (완료)
**구현 완료**
- ✅ 회원가입 / 로그인 / 로그아웃
- ✅ OAuth2 소셜 로그인 (Google, Kakao)
- ✅ 마이페이지 (경매 목록, 입찰 내역, 페이징)

---

### ✅ 3. 실시간 기능 (완료)
**구현 완료**
- ✅ WebSocket 기반 실시간 입찰 반영
- ✅ Redis Pub/Sub를 통한 이벤트 전파
- ✅ 입찰 내역 및 최고가 실시간 업데이트

---

### ✅ 4. 스케줄러 기반 자동 낙찰 (완료)
**구현 완료**
- ✅ 경매 종료 시간 도달 시 자동 낙찰 처리
- ✅ 경매 상태 자동 관리

---

### ✅ 5. 동시성 및 확장성 개선 (완료)
**구현 완료**
- ✅ Redis 기반 캐시 (Spring Cache)
- ✅ Redis 분산락 (Distributed Lock)
- ✅ JPA Optimistic Lock (@Version)
- ✅ TransactionAwareCacheManagerProxy 적용
- ✅ 락 계층 분리
- ✅ AWS 인프라 (RDS, EC2 Redis, S3, CloudFront)

---

### ✅ 6. 로깅 및 모니터링 (완료)
**구현 완료**
- ✅ Spring AOP 기반 Logging Aspect

---

### 🚧 7. 진행 예정 (추후 개발)
**예정 기능**
- 테스트 코드 작성
  - 단위(Unit) 테스트
  - 통합(Integration) 테스트
  - 경매 비즈니스 로직 검증
- 알림 시스템 (입찰, 낙찰, 경매 종료 알림)
- 결제 시스템 연동
- 검색 및 필터링 기능 강화
- MQ(Kafka, RabbitMQ 등) 기반 비동기 이벤트 처리 (선택적)