package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {
    private long productId;
    private String productName;
    private String productCategory;
    private float productDiscount;
    private String productDescription;
    private double productPrice;
    private double productOldPrice;
    private ArrayList<byte[]> productImages;
    private ArrayList<ColorSizeQuantityCombination> colorSizeQuantityCombinations;


}
