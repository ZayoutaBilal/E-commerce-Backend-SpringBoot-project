package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Discount;
import app.backend.click_and_buy.repositories.DiscountRepository;
import app.backend.click_and_buy.request.DiscountRequest;
import app.backend.click_and_buy.responses.DiscountOverview;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        return isEnded ? discountRepository.findAllByEndDateIsBefore(LocalDate.now()).stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList())
                : discountRepository.findAllByEndDateIsAfter(LocalDate.now()).stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList());
    }

    public List<DiscountOverview> getAllDiscounts() {
        return  discountRepository.findAll().stream().map(discount -> modelMapper.map(discount, DiscountOverview.class))
                .collect(Collectors.toList());
    }

    public List<DiscountProjection> getAllDiscounts_Projection() {
        return discountRepository.findAllProjectedBy();
    }


    public Discount saveDiscount(DiscountRequest discountRequest){
        return discountRepository.save(modelMapper.map(discountRequest,Discount.class));
    }

    public void updateDiscount(DiscountRequest discountRequest) {
        Discount discount = discountRepository.findByDiscountId(discountRequest.getDiscountId());
        if(Objects.isNull(discount))
            throw new IllegalArgumentException("Discount not found with ID: " + discountRequest.getDiscountId());
        modelMapper.map(discountRequest, discount);
        discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(long id){
        discountRepository.detachDiscountFromProducts(id);
        discountRepository.deleteById(id);
    }




}
