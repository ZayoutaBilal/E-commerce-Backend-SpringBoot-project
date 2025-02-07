package app.backend.click_and_buy.services;

import app.backend.click_and_buy.request.CreateProduct;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductVariation;
import app.backend.click_and_buy.repositories.ProductVariationRepository;
import app.backend.click_and_buy.request.UpdateProduct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
public class ProductVariationService {


    private final ProductVariationRepository productVariationRepository;

    public ProductVariation findProductVariationsByAProductId(Long productId, String color, String size, int quantity) {
        return productVariationRepository.findFirstByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(productId, color, size, quantity);
    }

    public ProductVariation findProductVariationsById(Long id) {
        return productVariationRepository.findByProductVariationId(id);
    }

    public List<ProductVariation> findProductVariationsById(List<Long> ids) {
        return productVariationRepository.findAllById(ids);

    }

    public void updateProductVariation(ProductVariation productVariation) {
        productVariationRepository.save(productVariation);
    }

    public void updateAllProductVariation(List<ProductVariation> productVariations) {
        productVariationRepository.saveAll(productVariations);
    }

    public ArrayList<ProductVariation> findAllProductVariationsByProduct(Product product) {
        return new ArrayList<>(productVariationRepository.findByProduct(product));
    }

    public void saveAll(List<ProductVariation> productVariation) {
        productVariationRepository.saveAll(productVariation);
    }

    public void addVariationsToProduct(List<CreateProduct.Variation> variations, Product product){
        List<ProductVariation> productVariations = variations.stream()
                .map(element -> {
                    ProductVariation pv = new ProductVariation();
                    pv.setColor(element.getColor());
                    pv.setQuantity(element.getQuantity());
                    pv.setSize(element.getSize());
                    pv.setProduct(product);
                    return pv;
                }).toList();

        saveAll(productVariations);
    }

    public void deleteAllByVariationId(List<Long> variationIds) {
        List<ProductVariation> variations = productVariationRepository.findAllById(variationIds);
        variations.forEach(variation -> variation.setDeleted(true));
        updateAllProductVariation(variations);
    }

    public void updateProductVariationQuantity(List<UpdateProduct.UpdatedVariation> updatedVariations) {
        List<ProductVariation> productVariations = findProductVariationsById(
                updatedVariations.stream()
                        .map(UpdateProduct.UpdatedVariation::getProductVariationId)
                        .toList()
        );
        productVariations.forEach(variation -> updatedVariations.stream()
                .filter(update -> update.getProductVariationId().equals(variation.getProductVariationId())) // Corrected filter condition
                .findFirst()
                .ifPresent(update -> variation.setQuantity(update.getQuantity())));
        saveAll(productVariations);
    }




}
