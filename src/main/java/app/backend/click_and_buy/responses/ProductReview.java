package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductReview {

    private String username;
    private String comment;
    private int stars;
    private byte[] image;
    private LocalDateTime creationDate;
}
