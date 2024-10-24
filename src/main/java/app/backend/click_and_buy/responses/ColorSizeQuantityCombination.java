package app.backend.click_and_buy.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorSizeQuantityCombination {
    private String size;
    private Map<String, Integer> colorQuantityMap;
}
