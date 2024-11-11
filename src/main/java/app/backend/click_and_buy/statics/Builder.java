package app.backend.click_and_buy.statics;

import app.backend.click_and_buy.entities.CartItem;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.entities.ProductImage;
import app.backend.click_and_buy.responses.ProductCart;
import app.backend.click_and_buy.responses.ProductOverview;
import app.backend.click_and_buy.services.ProductImageService;
import java.util.ArrayList;
import java.util.List;

public class Builder {

    public static List<ProductOverview> buildProductResponseList(List<Product> productList, ProductImageService productImageService){
        List<ProductOverview> productOverviewList =new ArrayList<>();
        for (Product product : productList) {
            ProductImage productImage = productImageService.getOneProductImageByProduct(product);
            productOverviewList.add(
                    ProductOverview.builder()
                            .name(product.getName())
                            .id(product.getProductId())
                            .newPrice(product.getPrice())
                            .oldPrice(product.getOldPrice() != null ? product.getOldPrice() : 0.0)
                            .image(productImage != null ? productImage.getImage() : new byte[0])
                            .category(product.getCategory().getName())
                            .build()
            );
        }
        return productOverviewList;
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
