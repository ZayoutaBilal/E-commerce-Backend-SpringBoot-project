package app.backend.click_and_buy.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForgetPassword {

    @Email(message = "A valid email address is required")
    @NotBlank(message = "Email cannot be null or blank")
    private String email;

    @NotBlank(message = "New password cannot be null or blank")
    private String newPassword;

    @NotBlank(message = "Code cannot be null or blank")
    private String code;
}
