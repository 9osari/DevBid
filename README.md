# DevBid - 실시간 경매 플랫폼

> 동시성 제어와 실시간 통신을 중점으로 구현한 경매 시스템
<br>

## 핵심 구현

### 동시성 제어
다수 사용자가 동시에 입찰할 때 데이터 정합성을 보장하기 위해 Redis 분산락과 JPA Optimistic Lock을 함께 적용했습니다.    
락 적용 과정에서 발생한 데드락 문제는 트랜잭션과 락의 계층을 분리하여 해결했습니다.
- [Redis 분산락과 Pub/Sub으로 멀티서버 동시성 제어](https://9osari.netlify.app/posts/redis-%EB%B6%84%EC%82%B0%EB%9D%BD%EA%B3%BC-pubsub-%EB%A9%80%ED%8B%B0%EC%84%9C%EB%B2%84-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4/)
- [낙관적 락 적용과 데드락](https://9osari.netlify.app/posts/%EB%82%99%EA%B4%80%EC%A0%81-%EB%9D%BD-%EC%A0%81%EC%9A%A9%EA%B3%BC-%EB%8D%B0%EB%93%9C%EB%9D%BD/)

### 실시간 통신
WebSocket과 STOMP를 활용해 입찰 현황을 실시간으로 브로드캐스트합니다.    
멀티 서버 환경에서 모든 접속자에게 메시지를 전파하기 위해 Redis Pub/Sub을 도입했습니다.
- [WebSocket으로 실시간 경매 구현하기](https://9osari.netlify.app/posts/websocket%EC%9C%BC%EB%A1%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EA%B2%BD%EB%A7%A4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0/)
- [멀티 서버 환경에서 Redis Pub/Sub이 필요한 이유](https://9osari.netlify.app/posts/websocket%EC%9D%98-%ED%95%9C%EA%B3%84%EC%99%80-redis-%ED%95%84%EC%9A%94%ED%95%9C-%EC%9D%B4%EC%9C%A0/)

### 이미지 업로드
S3 Presigned URL을 통해 클라이언트가 서버를 거치지 않고 직접 업로드하도록 구현했습니다. CloudFront를 연동하여 이미지 전송 속도를 개선했습니다.
- [S3 Presigned URL](https://9osari.netlify.app/posts/aws-s3-presignedurl/)

### 인증/인가
Spring Security 기반 세션 인증과 OAuth2 소셜 로그인(Google, Kakao)을 구현했습니다.
- [카카오와 구글 소셜로그인 구현하기](https://9osari.netlify.app/posts/springsecurity-%EC%B9%B4%EC%B9%B4%EC%98%A4%EA%B5%AC%EA%B8%80-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8/)

---

## 트러블슈팅

### Redis 도입 후 캐시 정합성 문제
Redis 캐시 적용 후 트랜잭션 롤백 시에도 캐시가 갱신되는 문제가 발생했습니다. TransactionAwareCacheManagerProxy를 적용하여 트랜잭션 커밋 이후에만 캐시가 갱신되도록 해결했습니다.
- [Redis 도입 후 마주한 문제와 해결](https://9osari.netlify.app/posts/redis-%EB%8F%84%EC%9E%85-%ED%9B%84-%EB%A7%88%EC%A3%BC%ED%95%9C-%EB%AC%B8%EC%A0%9C%EC%99%80-%ED%95%B4%EA%B2%B0/)

### Entity 설계 개선
UserEntity가 너무 많은 책임을 가지고 있어 SRP/DIP 위반 문제가 있었습니다. 도메인 역할을 분리하고 의존 방향을 정리하여 리팩토링했습니다.
- [UserEntity SRP/DIP 위반과 해결](https://9osari.netlify.app/posts/srp-dip-%EC%9C%84%EB%B0%98%EA%B3%BC%ED%95%B4%EA%B2%B0/)

### 카테고리 조회 N+1 문제
계층형 카테고리 조회 시 N+1 쿼리가 발생했습니다. Fetch Join과 쿼리 구조 개선으로 해결했습니다.
- [카테고리 트리 N+1 최적화](https://9osari.netlify.app/posts/%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC-%ED%8A%B8%EB%A6%AC-n+1-%EC%B5%9C%EC%A0%81%ED%99%94/)



## 기술 스택
| 영역 | 기술 |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2, Spring Security, JPA |
| **Database** | MySQL 8.0 (AWS RDS) |
| **Cache & Lock** | Redis - 분산락, Spring Cache, Pub/Sub |
| **Realtime** | WebSocket (SockJS + STOMP) |
| **Infra** | AWS EC2, RDS, S3, CloudFront |
| **Frontend** | Thymeleaf, HTML/CSS/JS |

## 문서
- [아키텍처](docs/Architecture.md)
- [요구사항](docs/Product-Requirements.md)
- [Use Case](docs/UseCase.md)
- [📝 블로그 - 트러블슈팅 전체 보기](https://9osari.netlify.app/)
