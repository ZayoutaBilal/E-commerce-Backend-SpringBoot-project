package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountOverview {

    private Long discountId;
    private String name;
    private String description;
    private Float percent;
    private LocalDate endDate;
    private LocalDate startDate;
    private LocalDateTime createdAt;
}
