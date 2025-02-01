package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Discount;
import app.backend.click_and_buy.repositories.DiscountRepository;
import app.backend.click_and_buy.responses.DiscountOverview;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;

    public Discount getDiscountById(long id) {
        return discountRepository.findByDiscountId(id);
    }

    public List<DiscountOverview> getAllDiscountsByEndDate(boolean isEnded) {
        return isEnded ? discountRepository.findAllByEndDateIsBefore(LocalDateTime.now()).stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList())
                : discountRepository.findAllByEndDateIsAfter(LocalDateTime.now()).stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList());
    }

    public List<DiscountOverview> getAllDiscounts() {
        return  discountRepository.findAll().stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList());
    }

    public List<DiscountProjection> getAllDiscounts_Projection() {
        return discountRepository.findAllProjectedBy();
    }


}
