package org.devbid.product.application;


import org.devbid.product.domain.Product;
import org.devbid.product.dto.ProductRegistrationRequest;

import java.util.List;

public interface ProductService {
    void registerProduct(ProductRegistrationRequest product);

    List<Product> findAllProducts();
    List<Product> findAllProductsBySellerId(Long seller);

    long getProductCount();
}
