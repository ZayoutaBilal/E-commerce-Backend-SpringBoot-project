package app.backend.click_and_buy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerDTO {



    @NotBlank(message = "First name cannot be null or blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be null or blank")
    private String lastName;

    private LocalDate birthday;

    private String gender;

    private String address;

    @NotBlank(message = "Phone number cannot be null or blank")
    private String phone;

    @NotBlank(message = "City cannot be null or blank")
    private String city;



}
