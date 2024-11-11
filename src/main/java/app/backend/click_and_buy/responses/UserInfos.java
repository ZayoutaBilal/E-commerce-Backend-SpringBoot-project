package app.backend.click_and_buy.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserInfos {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String username;
    private String city;
    private LocalDate birthday;
    private byte[] picture;
}
