package app.backend.click_and_buy.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

    @NotBlank(message = "Message cannot be null or blank")
    private String message;

    @NotBlank(message = "Email cannot be null or blank")
    private String email;

    private String name;


}
