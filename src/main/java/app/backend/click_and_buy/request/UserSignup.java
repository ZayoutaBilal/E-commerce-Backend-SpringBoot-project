package app.backend.click_and_buy.request;

import app.backend.click_and_buy.dto.CustomerDTO;
import app.backend.click_and_buy.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Data
@AllArgsConstructor
@Builder
public class UserSignup {

    @Valid
    @NotNull(message = "User data cannot be null")
    private UserDTO user;

    @Valid
    @NotNull(message = "Customer data cannot be null")
    private CustomerDTO customer;

}
