package app.backend.click_and_buy.statics;

import app.backend.click_and_buy.entities.CartItem;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductImage;
import app.backend.click_and_buy.responses.ProductCart;
import app.backend.click_and_buy.responses.ProductOverview;
import app.backend.click_and_buy.services.ProductImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Builder {

    public static Page<ProductOverview> buildProductResponseList(Page<Product> productPage,
                                                                 ProductImageService productImageService) {

        List<ProductOverview> productOverviewList = productPage.getContent().stream()
                .map(product -> {
                    ProductImage productImage = productImageService.getOneProductImageByProduct(product);
                    return ProductOverview.builder()
                            .name(product.getName())
                            .id(product.getProductId())
                            .newPrice(product.getPrice())
                            .oldPrice(Objects.requireNonNullElse(product.getOldPrice(), 0.0))
                            .image(Objects.requireNonNullElse(productImage.getImage(), new byte[0]))
                            .category(product.getCategory().getName())
                            .stars((product.getRating() != null ? product.getRating().getAverageStars() : 0.0))
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productOverviewList, productPage.getPageable(), productPage.getTotalElements());
    }


    public static ProductCart buildProductCart(CartItem cartItem, byte[] image) {
        ProductCart productCart=new ProductCart();
        productCart.setItemId(cartItem.getCartItemId());
        productCart.setQuantity(cartItem.getQuantity());
        productCart.setName(cartItem.getProductVariation().getProduct().getName());
        productCart.setPrice(cartItem.getProductVariation().getProduct().getPrice());
        productCart.setOldPrice(cartItem.getProductVariation().getProduct().getOldPrice());
        productCart.setSize(cartItem.getProductVariation().getSize());
        productCart.setImage(image);
        productCart.setColor(cartItem.getProductVariation().getColor());
        return productCart;
    }
}
