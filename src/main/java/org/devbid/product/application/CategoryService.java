package org.devbid.product.application;


import org.devbid.product.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategoryTree();

}
