package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductImage;
import app.backend.click_and_buy.repositories.ProductImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductImageService {


    private final ProductImageRepository productImageRepository;

    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);
    }

    public void saveAll(List<ProductImage> productImageList) {
        productImageRepository.saveAll(productImageList);
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

    public void addImagesToProduct(List<MultipartFile> images,Product product){
        List<ProductImage> productImages = images.stream()
                .map(element -> {
                    ProductImage pi = new ProductImage();
                    try { pi.setImage(element.getBytes());
                    } catch (IOException ignored) {}
                    pi.setProduct(product);
                    return pi;
                })
                .collect(Collectors.toList());

        saveAll(productImages);
    }

    public void deleteAllById(List<Long> productImages){
        productImageRepository.deleteAllById(productImages);
    }


}
