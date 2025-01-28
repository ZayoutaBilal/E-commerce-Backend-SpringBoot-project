package app.backend.click_and_buy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductManagement {


    private Long productId;
    private String name;
    private Double price;
    private Double oldPrice;
    private String description;
    private String information;
    private Long category;
    private Long discount;
    private List<Variation> variations;
    private List<Image> images;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Variation {
        private String size;
        private String color;
        private int quantity;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Image {
        private byte[] url;
        private long id;
    }

}


