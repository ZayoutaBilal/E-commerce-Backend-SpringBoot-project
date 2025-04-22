package app.backend.click_and_buy.responses;

import app.backend.click_and_buy.entities.Customer;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.enums.Roles;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserInfos {
    private Long userId;
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
    private boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Roles roles;

    public static UserInfos build(User user){
        return UserInfos.builder()
                .userId(user.getUserId())
                .firstName(user.getCustomer().getFirstName())
                .lastName(user.getCustomer().getLastName())
                .address(user.getCustomer().getAddress())
                .city(user.getCustomer().getCity())
                .phone(user.getCustomer().getPhone())
                .email(user.getEmail())
                .gender(user.getCustomer().getGender())
                .birthday(user.getCustomer().getBirthday())
                .username(user.getUsername())
                .picture(user.getCustomer().getPicture())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .roles(Roles.fromStrings(user.getRoles()))
                .build();
    }
}
