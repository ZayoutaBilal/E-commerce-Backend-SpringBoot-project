package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductImage;
import app.backend.click_and_buy.repositories.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);
    }

    public ProductImage getOneProductImageByProduct(Product product) {
        return productImageRepository.findFirstByProduct(product);
    }

    public ArrayList<ProductImage> getProductImagesByProduct(Product product) {
        List<ProductImage> images = productImageRepository.findByProduct(product);
        return new ArrayList<>(images);
    }


    public ArrayList<byte[]> getImagesFromProductImages(ArrayList<ProductImage> productImage) {
        ArrayList<byte[]> images = new ArrayList<>();
        for (ProductImage image: productImage) {
            images.add(image.getImage());
        }
        return images;
    }


}
