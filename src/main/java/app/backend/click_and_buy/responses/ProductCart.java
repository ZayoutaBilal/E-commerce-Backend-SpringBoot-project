package app.backend.click_and_buy.responses;

import app.backend.click_and_buy.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCart {

    private long itemId;
    private String name;
    private double price;
    private Double oldPrice;
    private int quantity;
    private String size;
    private String color;
    private byte[] image;



}
