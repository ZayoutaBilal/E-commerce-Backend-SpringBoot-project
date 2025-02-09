package app.backend.click_and_buy.request;

import lombok.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class UpdateProduct extends CreateProduct {

    private Long productId;
    private List<Long> deletedImages;
    private List<Long> deletedVariations;
    private List<UpdatedVariation> updatedVariations;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdatedVariation {
        private Long productVariationId;
        private int quantity;
    }


}
