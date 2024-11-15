package app.backend.click_and_buy.enums;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Numbers {
    PARTIAL_PRODUCT_LIST_SIZE(20,0f, 0L,0D),
    NUMBER_OF_PRODUCTS_COULD_EXPORTED_FROM_DB_FOR_A_USER_BEHAVIOR(100,0f, 0L,0D),
    NUMBER_OF_CATEGORIES_COULD_EXPORTED_FROM_DB_FOR_A_USER_BEHAVIOR(50,0f, 0L,0D);
    private int intValue;
    private float floatValue;
    private long longValue;
    private double doubleValue;
}
