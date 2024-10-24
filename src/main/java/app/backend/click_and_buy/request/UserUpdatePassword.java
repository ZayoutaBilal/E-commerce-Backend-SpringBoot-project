package app.backend.click_and_buy.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePassword {

    @NotBlank(message = "Old password cannot be null or blank")
    private String oldPassword;
    @NotBlank(message = "New password cannot be null or blank")
    private String newPassword;

}
