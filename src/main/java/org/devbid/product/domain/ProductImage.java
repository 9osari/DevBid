package org.devbid.product.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "product_image")
@Getter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String imageKey;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private ProductImage(Product product, String imageKey, int sortOrder) {
        this.product = product;
        this.imageKey = imageKey;
        this.sortOrder = sortOrder;
    }

    protected ProductImage() {}

    public static ProductImage create(Product product, String imageKey, int sortOrder) {
        return new ProductImage(product, imageKey, sortOrder);
    }
}
