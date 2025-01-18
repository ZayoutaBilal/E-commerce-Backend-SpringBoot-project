package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.dto.DiscountProjection;
import app.backend.click_and_buy.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<DiscountProjection> findAllProjectedBy();
    Discount findByDiscountId(long id);
}
