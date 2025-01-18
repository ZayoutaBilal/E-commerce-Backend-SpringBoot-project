package app.backend.click_and_buy.dto;

public interface DiscountProjection {
    Long getDiscountId();
    String getName();
    Float getPercent();
}
