package app.backend.click_and_buy.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsCategory {

    private String categoryName;

    private String origin;

    private int page;
}
