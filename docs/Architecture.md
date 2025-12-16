# 🏗 아키텍처

## 전체 구조
```mermaid
graph TB
    A[Web Browser] --> B[Spring Boot Application]
    A -.->|WebSocket| B
    B --> C[(MySQL RDS)]
    B --> D[(Redis)]
    B --> E[S3/CloudFront]

    subgraph "Spring Boot 내부"
        F[Controller Layer]
        G[Service Layer]
        H[Repository Layer]
        I[WebSocket Handler]
        J[Scheduler]
        K[Logging Aspect]
        L[OAuth2 Handler]
    end

    F --> G
    G --> H
    H --> C
    I --> G
    J --> G
    K -.->|AOP| G
    L --> G
    G --> D
```

## DB ERD
![ERD](/docs/DB%20ERD.png)

## 레이어 구조

- **Controller Layer**
    - HTTP 요청 처리, 세션 관리, DTO 변환
    - OAuth2 인증 처리 (Google, Kakao)
- **Service Layer**
    - 비즈니스 로직, 동시성 제어, 캐싱 처리
    - Redis 분산락 기반 동시성 제어
- **Repository Layer**
    - JPA 기반 DB 접근
    - Optimistic Lock 적용
- **WebSocket Handler**
    - 실시간 이벤트 브로드캐스트
    - Redis Pub/Sub 기반 메시지 전파
    - 입찰 현황 실시간 전송
- **Scheduler**
    - 경매 종료 시점 제어, 낙찰 처리
    - @Scheduled 기반 자동화
- **Logging Aspect**
    - AOP 기반 로깅 처리
    - 주요 비즈니스 로직 실행 추적

---

## 사용 기술
- **인증/인가**:
    - Spring Security + 세션 기반 인증
    - OAuth2 소셜 로그인 (Google, Kakao)
- **캐싱**:
    - ~~ConcurrentHashMap~~ → **Redis Spring Cache**
    - TransactionAwareCacheManagerProxy 적용
- **동시성 제어**:
    - Redis 분산락 (Distributed Lock)
    - JPA Optimistic Lock (@Version)
- **실시간**:
    - WebSocket + Redis Pub/Sub 기반 브로드캐스트
- **인프라**:
    - AWS RDS (MySQL, Seoul Region)
    - AWS EC2 Redis
    - AWS S3 + CloudFront (이미지 저장/전송)
- **로깅**:
    - Spring AOP 기반 Logging Aspect

---

## MVP 범위

1. **회원 관리**
    - 회원가입 / 로그인 / 로그아웃
    - 세션 기반 인증/인가
    - OAuth2 소셜 로그인 (Google, Kakao)
    - 마이페이지 (경매 목록, 입찰 내역)
2. **경매 기능**
    - 경매 등록 / 조회 / 수정 / 삭제 (CRUD)
    - 입찰 / 즉시구매
    - 재경매 (동일 Product로 새로운 Auction 생성)
    - Redis 기반 최고가 캐싱
3. **실시간 기능**
    - WebSocket + Redis Pub/Sub 기반 입찰 현황 반영
    - 실시간 경매 정보 업데이트
4. **자동화**
    - 스케줄러로 경매 종료 → 낙찰자 결정
    - 경매 상태 자동 관리

---

## 시퀀스 다이어그램 (입찰 프로세스)

```mermaid
sequenceDiagram
    participant User as 사용자(브라우저)
    participant WS as WebSocket
    participant Ctrl as AuctionController
    participant Svc as AuctionService
    participant Redis as Redis
    participant Repo as AuctionRepository
    participant DB as MySQL RDS
    participant PubSub as Redis Pub/Sub

    User->>WS: 입찰 요청 (bid)
    WS->>Ctrl: 메시지 전달
    Ctrl->>Svc: placeBid(auctionId, bidRequest)

    Note over Svc,Redis: "Redis 분산락 획득"
    Svc->>Redis: SETNX lock:auction:{id}
    Redis-->>Svc: Lock 획득 성공

    Svc->>Repo: 현재 최고가 조회
    Repo->>DB: SELECT current_price FROM AUCTIONS
    DB-->>Repo: 현재가 반환
    Repo-->>Svc: 현재가 반환

    Svc->>Svc: 최소 입찰 단위 검증 / 상태 확인
    Svc->>Repo: 입찰 내역 저장 (INSERT INTO BIDS)
    Svc->>Repo: 경매 현재가 갱신 (UPDATE AUCTIONS.current_price)
    Repo->>DB: Commit

    DB-->>Repo: 저장 완료
    Repo-->>Svc: 완료 반환

    Svc->>Redis: 캐시 갱신
    Svc->>PubSub: 입찰 이벤트 발행

    Note over Svc,Redis: "Redis 분산락 해제"
    Svc->>Redis: DEL lock:auction:{id}

    Svc-->>Ctrl: 입찰 성공 응답
    Ctrl-->>WS: 결과 전송
    PubSub-->>WS: 이벤트 전파
    WS-->>User: 최신가 브로드캐스트
```