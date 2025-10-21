package org.devbid.product.application;


import org.devbid.product.domain.Product;
import org.devbid.product.dto.ProductListResponse;
import org.devbid.product.dto.ProductRegistrationRequest;

import java.util.List;

public interface ProductService {
    void registerProduct(ProductRegistrationRequest product);

    List<ProductListResponse> findAllWithImages();
    List<ProductListResponse> findAllProductsBySellerId(Long seller);

    long getProductCount();
}
