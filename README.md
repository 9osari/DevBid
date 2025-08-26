### 프로젝트 명

**DevBid** (데브비드)

### 구상

개발자들을 위한 실시간 경매 플랫폼

### Backend

- **Java 21 LTS**
- **Spring Boot**
- **Spring Data JPA**
- **MySQL** (DB)
- **Redis** (캐시, 세션, 실시간 데이터)

### Frontend

- **Thymeleaf** (초기)
- **JavaScript** (실시간)
- **Bootstrap** (UI)

### 실시간 통신

- **WebSocket** (실시간 경매, 채팅)

# ‼️‼️설계‼️‼️를 잘하자

1. 메세지 설계를 하자
2. 메세지 설계를 잘하면 메세지가 각각 하나의 메서드로 나온다?!
3. 비슷한 성격의 메세지끼리 모아 하나의 책임으로 묶음
4. 해당 책임을 가질 객체의 이름을 추상적으로 지어 만든다 → 역할 생성

## 회원가입 요구사항

- 사용자가 회원가입을 요청한다.
- 시스템은 id와 email의 중복 여부를 확인한다.
  - 이미 존재하는 경우 가입불가.
- 신규회원을 저장한다.
  - 회원상태: 활성
  - 생성일시: 가입일자
- 가입 완료 알림을 띄운다.

## 메세지 설계

- User → UserServiceImpl : 나 가입 원해
- UserServiceImpl → UserDuplicateValidator : 이 ID 중복 아닌가?
- UserDuplicateValidator → UserServiceImpl : 중복 아님
- UserServiceImpl : User 객체 생성 (new User)
- UserServiceImpl → UserRepository : 저장해(User)

가입완료 알림창 찍

## 책임은?

- **User**
    - 회원 객체
- **UserServiceImpl**
    - 가입 흐름만 담당
- **UserDuplicateValidator**
    - 중복 여부 확인만
- **UserRepository**
    - db 접근만
- **UserStatus**
    - 유저 상태값 enum
- **UserController**
    - http 요청 처리


회원가입 참고용

[https://rastalion.dev/회원-가입-및-로그인을-위한-테이블-설계/](https://rastalion.dev/%ED%9A%8C%EC%9B%90-%EA%B0%80%EC%9E%85-%EB%B0%8F-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%9D%84-%EC%9C%84%ED%95%9C-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%84%A4%EA%B3%84/)