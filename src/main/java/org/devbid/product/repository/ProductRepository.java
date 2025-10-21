package org.devbid.product.repository;


import org.devbid.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN p.seller " +
            "WHERE p.seller.id = :sellerId")
    List<Product> findBySellerId(Long sellerId);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN p.seller")
    List<Product> findAllWithImages();

}
