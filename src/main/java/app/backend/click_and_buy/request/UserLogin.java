package app.backend.click_and_buy.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    @NotBlank(message = "Login cannot be null or blank")
    private String login;
    @NotBlank(message = "Password cannot be null or blank")
    private String password;

}
