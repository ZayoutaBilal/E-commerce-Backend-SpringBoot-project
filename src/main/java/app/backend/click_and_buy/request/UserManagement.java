package app.backend.click_and_buy.request;

import app.backend.click_and_buy.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserManagement {

    @NotBlank(message = "Username cannot be null or blank")
    private String username;

    @Email
    @NotBlank(message = "Email cannot be null or blank")
    private String email;

    @NotBlank(message = "Phone number cannot be null or blank")
    private String phone;

    @NotBlank(message = "User must have some role")
    private Roles roles;

    private boolean active;

}
