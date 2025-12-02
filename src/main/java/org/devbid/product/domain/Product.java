package org.devbid.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.user.domain.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductName productName;

    @Embedded
    private Description description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Auction> auctions = new  ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition", nullable = false)
    private ProductCondition condition = ProductCondition.WORN;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private ProductStatus saleStatus = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    public Product(ProductName productName,
                   Description description,
                   ProductImage productImage,
                   Category category,
                   ProductCondition condition,
                   ProductStatus saleStatus,
                   User seller)
    {
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.seller = seller;
    }

    public static Product of(ProductName productName, Description description, Category category, ProductCondition condition, User seller) {
        Product product = new Product();
        product.productName = productName;
        product.description = description;
        product.category = category;
        product.condition = condition;
        product.saleStatus = ProductStatus.ACTIVE;
        product.seller = seller;
        return product;
    }


    public boolean updateProductInfo(
            String productName,
            String description,
            String condition
    ) {
        boolean isUpdated = false;
        if (productName != null && !productName.isBlank()) {
            //from() 정적 팩토리 메서드 사용
            //장점:
            //의미가 명확: "String으로부터 ProductName을 만든다"
            //캡슐화: 내부 구현 숨김
            //유연성: 나중에 검증 로직 추가 가능
            //ProductName.from(String) → ProductName 객체 생성
            //Description.from(String) → Description 객체 생성
            this.productName = ProductName.from(productName);
            isUpdated = true;
        }

        if (description != null) {
            //from() 정적 팩토리 메서드 사용
            this.description = Description.from(description);
            isUpdated = true;
        }

        if (condition != null) {
            this.condition = ProductCondition.valueOf(condition);
            isUpdated = true;
        }

        return  isUpdated;
    }



    public void updateMainImage(Long keepMainImageId, String newMainImageKey) {
        ProductImage currentMainImage = this.images.stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .orElse(null);

        // 새 이미지 업로드 한 경우
        if (newMainImageKey != null && !newMainImageKey.isBlank()) {
            if (currentMainImage != null) {
                this.images.remove(currentMainImage);
            }
            // 새 이미지 추가
            ProductImage newMainImage = ProductImage.create(this, newMainImageKey, 1);
            this.images.add(newMainImage);
            this.touch();
        }
        // 아무것도 안 하면 기존 이미지 유지
    }

    public void updateSubImages(List<Long> keepSubImageIds, List<String> subImageKeys) {
        List<ProductImage> currentSubImages = this.images.stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .toList();

        //새 서브 이미지 업로드 한 경우
        if (subImageKeys != null && !subImageKeys.isEmpty()) {
            if (currentSubImages != null) {
                this.images.removeAll(currentSubImages);
            }

            // 모든 서브 이미지 추가 (sortOrder는 2부터 시작)
            int sortOrder = 2;
            for (String imageKey : subImageKeys) {
                ProductImage newSubImage = ProductImage.create(this, imageKey, sortOrder++);
                this.images.add(newSubImage);
                this.touch();
            }
        }
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void softDelete() {
        this.saleStatus = ProductStatus.DELETED;
    }
    
    public int activeAuctionCount() {
        return (int) auctions.stream()
            .filter(auction ->
                    auction.getStatus() == AuctionStatus.BEFORE_START ||
                    auction.getStatus() == AuctionStatus.ONGOING
            ).count();
    }

    public String getMainImageUrl() {
        return images.stream()
                .filter(img -> img.getSortOrder() == 1)
                .findFirst()
                .map(ProductImage::getImageKey)
                .orElse(null);
    }

    public List<String> getSubImageUrls() {
        return images.stream()
                .filter(img -> img.getSortOrder() > 1)
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(ProductImage::getImageKey)
                .toList();
    }

}