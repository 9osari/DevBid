package org.devbid.product.dto;

import lombok.Getter;
import org.devbid.product.domain.Category;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryDto {
    private Long id;
    private String name;
    private List<CategoryDto> children;

    public CategoryDto(Long id, String name, List<CategoryDto> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }

    public static CategoryDto of(Category category) {
        List<Category> children = category.getChildren();
        return new CategoryDto(
                category.getId(),
                category.getName(),
                children.stream()
                        .map(CategoryDto::of)   //자기자신 호출 (재귀)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "CategoryDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
