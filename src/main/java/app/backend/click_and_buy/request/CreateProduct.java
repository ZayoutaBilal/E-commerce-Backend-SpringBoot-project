package app.backend.click_and_buy.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProduct {

    private String name;
    private Double price;
    private Double oldPrice;
    private String description;
    private String information;
    private Long category;
    private Long discount;
    private List<Variation> variations;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Variation {
        private String size;
        private String color;
        private int quantity;
    }

}


