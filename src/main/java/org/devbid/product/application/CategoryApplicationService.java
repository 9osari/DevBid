package org.devbid.product.application;

import org.devbid.product.domain.Category;
import org.devbid.product.dto.CategoryDto;
import org.devbid.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryApplicationService implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getCategoryTree(Long parentId) {
        List<Category> categories = categoryRepository.findByLevel(parentId);
        return categories.stream().map(CategoryDto::of).collect(Collectors.toList());
    }
}
