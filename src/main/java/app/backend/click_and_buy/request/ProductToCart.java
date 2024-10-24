package app.backend.click_and_buy.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull(message = "Request body cannot be null")
public class ProductToCart {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    @NotBlank(message = "Color cannot be null or blank")
    private String color;

    @NotBlank(message = "Size cannot be null or blank")
    private String size;
}
