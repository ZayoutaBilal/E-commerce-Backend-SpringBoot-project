package app.backend.click_and_buy.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.OnMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyItemQuantity {

    @NotNull(message = "Item id cannot be null")
    private Long itemId;

    @NotNull(message = "New quantity cannot be null")
    private Integer newQuantity;
}
