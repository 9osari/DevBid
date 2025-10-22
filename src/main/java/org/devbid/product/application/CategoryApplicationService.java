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
    public List<CategoryDto> getCategoryTree() {
        // 전체 카테고리를 조회
        List<Category> allCategories = categoryRepository.findAllWithParent();

        // 최상위 카테고리(level=1)만 필터링
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParent() == null)
                .toList();

        // DTO 변환 시 children은 이미 로딩되어 있어 추가 쿼리 없음
        return rootCategories.stream()
                .map(CategoryDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> findAllCategoryTree() {
        List<Category> rootCategories = categoryRepository.findAllCategoryTree();
        return rootCategories.stream()
                .map(CategoryDto::of)
                .collect(Collectors.toList());
    }
}
