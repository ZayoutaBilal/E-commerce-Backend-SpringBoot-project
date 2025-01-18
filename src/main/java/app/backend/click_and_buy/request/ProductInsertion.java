package app.backend.click_and_buy.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductInsertion {
    private String name;
    private double price;
    private Object images;
    private String description;
    private String information;
    private long category;
    private long discount;
    private List<Variation> variations;


    @Data
    @AllArgsConstructor
    public static class Variation {
        private String size;
        private String color;
        private int quantity;
    }
}


