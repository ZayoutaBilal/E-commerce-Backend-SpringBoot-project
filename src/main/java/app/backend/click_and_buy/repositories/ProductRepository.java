package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByProductId(long id);
    List<Product> findByProductIdIn(Set<Long> ids);
    List<Product> findByCategoryNameContaining(String categoryName);
    List<Product> findByCategoryIsIn(List<Category> categories);
    List<Product> findByCategory(Category category);
    Page<Product> findByCategoryIsIn(Collection<Category> category, Pageable pageable);
    int countByCategory(Category category);
    Page<Product> findByOrderByCreatedAtDesc(Pageable pageable);
    Page<Product> findByRating_AverageStarsGreaterThanEqual(Double minRating, Pageable pageable);
}
