package app.backend.click_and_buy.repositories;


import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    ProductImage findFirstByProduct(Product product);
    List<ProductImage> findByProduct(Product product);
}
