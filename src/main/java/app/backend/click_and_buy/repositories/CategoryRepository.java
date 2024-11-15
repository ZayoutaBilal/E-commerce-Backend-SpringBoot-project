package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryId(long id);
    List<Category> findByNameContains(String categoryName);
    Category findByNameLikeAndParentCategoryId(String categoryName,Long parentCategoryId);
    List<Category> findByParentCategoryId(long parentId);
    List<Category> findByParentCategoryIdIn(List<Long> ids);
    List<Category> findByParentCategoryId(Long parentCategoryId);


}
