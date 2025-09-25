# Product & Auction 요구사항 정리

## 1. 사용자 관리
- 로그인된 사용자만 상품(경매) 등록 가능
- 사용자는 판매자/입찰자 역할

## 2. 상품(Product) 관리

### 상품 등록
- 상품명, 설명, 이미지
- 카테고리 (메인/서브)
- 상품 상태 정보

### 상품 상태
- ACTIVE (판매중)
- SOLD (판매완료)
- DELETED (삭제)
- DISABLED (숨김)

## 3. 경매(Auction) 관리

### 경매 설정
- 경매 시작가
- 경매 종료가 (reserve price?)
- 즉시낙찰가
- 경매 종료시간

### 경매 상태
- 경매전 (BEFORE_START)
- 경매중 (ONGOING)
- 경매완료 (ENDED)

## 4. 비즈니스 규칙

### 기본 규칙
- Product ↔ Auction = 1:1 관계
- 경매 종료 시 상태 동기화
- 각 엔티티는 독립적인 생명주기

### 경매 종료 관련
- 경매 종료 임박 시 **판매자는 연장 가능**
- 경매 종료되면 해당 상품의 경매는 완전히 끝
- 재판매 시 판매자가 새로운 경매로 다시 등록 필요

### 즉시낙찰
- 판매자가 즉시낙찰가 설정 시
- 구매자는 해당 가격으로 **즉시 구매 가능**
- 즉시낙찰 시 경매 즉시 종료

## 5. 추후 고려사항 (동시성 처리)
- 경매 연장 횟수/시간 제한
- 동시 입찰 처리
- 즉시낙찰 vs 일반 입찰 동시 처리
- 경매 연장 시 race condition

## 6. 설계 방향
- 상품 정보와 거래 로직 분리
  - Product: 상품 자체 정보 관리
  - Auction: 거래 방식 및 경매 로직 관리

## 7. 어캐하노
- 조회수는 상품 책임인지? 경매 책임인지? 중복 조회는 어떻게 처리할건지? ip기반? 엔티티 따로 뺴는건 맞는데 흠.. 아니면 외부서비스?
- 수정책임은 상품? 경매? 서비스단?

---

```mysql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    product_condition VARCHAR(50) NOT NULL DEFAULT 'WORN',
    sale_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    seller_id BIGINT NOT NULL,
    registration_date DATETIME NOT NULL,
    created_at datetime not null,
    updated_at datetime not null,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_products_seller
        FOREIGN KEY (seller_id) REFERENCES users(id)
);

CREATE TABLE product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL,

    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    level INT,
    parent_id BIGINT,

    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE auctions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    starting_price DECIMAL(19, 2) NOT NULL,
    current_price DECIMAL(19, 2) NOT NULL,
    buyout_price DECIMAL(19, 2) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    extended_count INT NOT NULL DEFAULT 0,
    auction_status VARCHAR(20) NOT NULL DEFAULT 'BEFORE_START',
    bid_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_auction_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_auction_status
        CHECK (auction_status IN ('BEFORE_START', 'ONGOING', 'ENDED')),

    CONSTRAINT chk_prices
        CHECK (starting_price > 0 AND current_price > 0 AND buyout_price > 0),

    CONSTRAINT chk_time_range
        CHECK (end_time > start_time)
);

CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    bid_amount DECIMAL(19, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_bid_auction
        FOREIGN KEY (auction_id) REFERENCES auctions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bid_user
        FOREIGN KEY (bidder_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_bid_amount
        CHECK (bid_amount > 0)
);
```