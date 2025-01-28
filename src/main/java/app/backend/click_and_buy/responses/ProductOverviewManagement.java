package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOverviewManagement {
    private long productId;
    private String name;
    private double price;
    private double oldPrice;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private byte[] image;
    private String categoryName;
    private float discountPercent;

}
