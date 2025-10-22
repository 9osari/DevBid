package org.devbid.product.application;


import org.devbid.product.dto.ProductListResponse;
import org.devbid.product.dto.ProductRegistrationRequest;
import org.devbid.product.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    void registerProduct(ProductRegistrationRequest product);

    void update(Long id, Long seller, ProductUpdateRequest product);

    List<ProductListResponse> findAllWithImages();
    List<ProductListResponse> findAllProductsBySellerId(Long seller);
    ProductListResponse findEditableByIdAndSeller(Long id, Long seller);

    long getProductCount();

    void deleteProductById(Long id);
}
