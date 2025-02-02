package app.backend.click_and_buy.request;

import app.backend.click_and_buy.groups.CreateDiscount;
import app.backend.click_and_buy.groups.UpdateDiscount;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {

    @NotNull(groups = UpdateDiscount.class, message = "Discount ID is required for updates")
    private Long discountId;

    @NotBlank(groups = {CreateDiscount.class,UpdateDiscount.class}, message = "Name is required")
    @Size(groups = {CreateDiscount.class,UpdateDiscount.class},max = 100, message = "Name must be less than 100 characters")
    private String name;

    private String description;

    @NotNull(groups = {CreateDiscount.class,UpdateDiscount.class},message = "Percent is required")
    @DecimalMin(groups = {CreateDiscount.class,UpdateDiscount.class},value = "0.0", message = "Percent must be greater than or equal to 0.0")
    private Float percent;

    @NotNull(groups = {CreateDiscount.class,UpdateDiscount.class},message = "Start date is required")
    @FutureOrPresent(groups = CreateDiscount.class,message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(groups = {CreateDiscount.class,UpdateDiscount.class},message = "End date is required")
    @Future(groups = CreateDiscount.class,message = "End date must be in the future")
    private LocalDate endDate;


}
