package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOverview {
    private long id;
    private String name;
    private String category;
    private double oldPrice;
    private double newPrice;
    private byte[] image;
    private double stars;

}
