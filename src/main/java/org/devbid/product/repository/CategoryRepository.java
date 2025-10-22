package org.devbid.product.repository;

import org.devbid.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 전체 카테고리 조회 (한 번의 쿼리)
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent")
    List<Category> findAllWithParent();

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findAllCategoryTree();

    Optional<Category> findById(Long id);
}
