package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductVariation;
import app.backend.click_and_buy.repositories.ProductVariationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductVariationService {

    @Autowired
    private ProductVariationRepository productVariationRepository;

    public ProductVariation findProductVariationsByAProductId(Long productId, String color, String size, int quantity) {
        return productVariationRepository.findFirstByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(productId, color, size, quantity);
    }

    public ProductVariation findProductVariationsById(Long id) {
        return productVariationRepository.findByProductVariationId(id);
    }

    public void updateProductVariation(ProductVariation productVariation) {
        productVariationRepository.save(productVariation);
    }

    public ArrayList<ProductVariation> findAllProductVariationsByProduct(Product product) {
        return new ArrayList<>(productVariationRepository.findByProduct(product));
    }


}
