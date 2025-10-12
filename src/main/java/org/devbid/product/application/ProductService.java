package org.devbid.product.application;


import org.devbid.product.dto.ProductRegistrationRequest;

public interface ProductService {
    void registerProduct(ProductRegistrationRequest product);
}
