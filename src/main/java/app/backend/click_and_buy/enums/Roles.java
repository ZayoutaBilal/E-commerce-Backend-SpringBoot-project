package app.backend.click_and_buy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Roles {

    CUSTOMER(List.of("ROLE_CUSTOMER")),
    CUSTOMER_SERVICE(List.of("ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER")),
    MANAGER(List.of("ROLE_MANAGER","ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER")),
    ADMIN(List.of("ROLE_ADMIN","ROLE_MANAGER","ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER"));

    private List<String> roles;

}
