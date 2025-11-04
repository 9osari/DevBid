package org.devbid.product.repository;


import org.devbid.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndSellerId(Long id, Long sellerid);

    Optional<Product> findById(Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN p.seller " +
            "WHERE p.seller.id = :sellerId " +
            "AND p.saleStatus <> org.devbid.product.domain.ProductStatus.DELETED " +
            "ORDER BY COALESCE(p.updatedAt, p.createdAt) DESC ")
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.seller " +
            "LEFT JOIN FETCH p.images " +
            "ORDER BY COALESCE(p.updatedAt, p.createdAt) DESC ")
    Page<Product> findAllWithImages(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN p.seller " +
            "WHERE p.seller.id = :sellerId AND p.id = :id")
    Optional<Product> findEditableByIdAndSeller(Long id, Long sellerId);

}
