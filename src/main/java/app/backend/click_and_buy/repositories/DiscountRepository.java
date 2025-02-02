package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<DiscountProjection> findAllProjectedBy();
    Discount findByDiscountId(long id);
    List<Discount> findAllByEndDateIsAfter(LocalDate localDate);
    List<Discount> findAllByEndDateIsBefore(LocalDate localDate);

    @Modifying
    @Query("UPDATE Product p SET p.discount = NULL WHERE p.discount.discountId = :discountId")
    void detachDiscountFromProducts(@Param("discountId") Long discountId);

}
