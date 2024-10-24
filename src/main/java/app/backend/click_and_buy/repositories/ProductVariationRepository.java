package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    List<ProductVariation> findByProductProductId(Long productId);

    ProductVariation findFirstByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(Long productId, String color, String size, int quantity);

    ProductVariation findByProductVariationId(Long productVariationId);

    List<ProductVariation> findByProduct(Product product);
}
