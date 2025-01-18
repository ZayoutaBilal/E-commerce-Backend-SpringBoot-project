package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Discount;
import app.backend.click_and_buy.repositories.DiscountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    public Discount getDiscountById(long id) {
        return discountRepository.findByDiscountId(id);
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public List<DiscountProjection> getAllDiscounts_Projection() {
        return discountRepository.findAllProjectedBy();
    }


}
