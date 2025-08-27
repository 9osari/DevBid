# 🏗 아키텍처

## 전체 구조
```mermaid
graph TB
    A[Web Browser] --> B[Spring Boot Application]
    A -.->|WebSocket| B
    B --> C[(MySQL)]
    
    subgraph "Spring Boot 내부"
        E[Controller Layer]
        F[Service Layer]  
        G[Repository Layer]
        H[WebSocket Handler]
        I[Scheduler]
    end
    
    E --> F
    F --> G
    G --> C
    H --> F
    I --> F
```

## 레이어 구조

- **Controller Layer**
    - HTTP 요청 처리, 세션 관리, DTO 변환
- **Service Layer**
    - 비즈니스 로직, 동시성 제어, 캐싱 처리
- **Repository Layer**
    - JPA 기반 DB 접근
- **WebSocket Handler**
    - 실시간 이벤트 브로드캐스트
    - 입찰 현황 실시간 전송
- **Scheduler**
    - 경매 종료 시점 제어, 낙찰 처리
    - @Scheduled 기반 자동화

---

## 사용 기술
- 인증: Spring Security 직접 구현
- 캐싱: **ConcurrentHashMap** → 단일 서버 환경
- 동시성 제어: `synchronized` 기반 → 기본 수준, 이후 DB Lock/Redis/MQ 고려
- 실시간: **WebSocket 단순 브로드캐스트**

---

## MVP 범위

1. **회원 관리**
    - 회원가입 / 로그인 / 로그아웃
    - 세션 기반 인증/인가
2. **경매 기능**
    - 등록 / 조회 / 입찰
    - 최고가 캐싱
3. **실시간 기능**
    - WebSocket을 통한 입찰 현황 반영
4. **자동화**
    - 스케줄러로 경매 종료 → 낙찰자 결정
    - PG API로 결제 시뮬레이션

---

## 시퀀스 다이어그램


```mermaid
sequenceDiagram
    participant User as 사용자(브라우저)
    participant WS as WebSocket
    participant Ctrl as AuctionController
    participant Svc as AuctionService
    participant Repo as AuctionRepository
    participant DB as MySQL

    User->>WS: 입찰 요청 (bid)
    WS->>Ctrl: 메시지 전달
    Ctrl->>Svc: placeBid(auctionId, bidRequest)

    Note over Svc: "synchronized(auctionId 별 lock)"
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
    Note over Svc: "synchronized 블록 해제"

    Svc-->>Ctrl: 입찰 성공 응답
    Ctrl-->>WS: 결과 전송
    WS-->>User: 최신가 브로드캐스트
```