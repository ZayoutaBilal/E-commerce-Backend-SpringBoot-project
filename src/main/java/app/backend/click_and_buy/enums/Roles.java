package app.backend.click_and_buy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Getter
@NoArgsConstructor
public enum Roles {

    CUSTOMER(List.of("ROLE_CUSTOMER")),
    CUSTOMER_SERVICE(List.of("ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER")),
    MANAGER(List.of("ROLE_MANAGER","ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER")),
    ADMIN(List.of("ROLE_ADMIN","ROLE_MANAGER","ROLE_CUSTOMER_SERVICE","ROLE_CUSTOMER"));

    private List<String> roles;

    Roles(List<String> roles) {
        this.roles = roles;
    }

    public static Roles fromStrings(List<String> roleStrings) {
        if (roleStrings == null || roleStrings.isEmpty()) {
            return null;
        }
        for (Roles role : Roles.values()) {
            if (new HashSet<>(role.getRoles()).containsAll(roleStrings)) {
                return role;
            }
        }

        throw new IllegalArgumentException("No matching Roles enum for: " + roleStrings);
    }
}
