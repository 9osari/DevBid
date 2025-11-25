package org.devbid.product.application;


import org.devbid.product.dto.ProductListResponse;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.product.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void registerProduct(ProductRegistrationRequest product);

    void update(Long id, Long seller, ProductUpdateRequest product);

    Page<ProductListResponse> findAllWithImages(Pageable pageable);
    Page<ProductListResponse> findAllProductsBySellerId(Long seller, Pageable  pageable);
    ProductListResponse findEditableByIdAndSeller(Long id, Long seller);

    long getProductCount();
    long countBySellerIdAndStatusNot(Long sellerId);

    void deleteProductById(Long id);
}
