package org.devbid.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devbid.infrastructure.common.BaseEntity;
import org.devbid.user.domain.User;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "product")
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

    @Embedded
    private RegistrationDate registrationDate;

}