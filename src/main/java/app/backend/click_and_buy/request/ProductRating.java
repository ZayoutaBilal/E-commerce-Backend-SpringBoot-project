package app.backend.click_and_buy.request;

import app.backend.click_and_buy.statics.ObjectValidator;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRating {

    @NotNull(message = "Product id must have a value")
    @Min(message = "Product must be greater or equals 1",value = 1)
    private int productId;
    private Integer ratingValue;
    private String comment;

    @AssertTrue(message = "Either rating value or comment must be provided, and rating value must be between 1 and 5 if provided")
    private boolean isValidRating() {
        if (getRatingValue() == null && !ObjectValidator.stringValidator(getComment())) {
            return false;
        }
        return getRatingValue() == null || (getRatingValue() >= 1 && getRatingValue() <= 5);
    }
}
