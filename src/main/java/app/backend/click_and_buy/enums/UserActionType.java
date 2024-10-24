package app.backend.click_and_buy.enums;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserActionType {

    VIEW(Long.class),     // Viewing a product, associated with product ID (Long)
    SEARCH(String.class), // Searching, associated with a search query (String)
    ADD_TO_CART(Long.class); // Adding to cart, associated with product ID (Long)

    private Class<?> detailType;

    public static UserActionType fromString(String action) {
        try {
            return UserActionType.valueOf(action.toUpperCase()); // Convert to enum ignoring case
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid action type: " + action); // Custom error handling
        }
    }
}
