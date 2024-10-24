package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findCartByCustomerCustomerId(Long customerId);
}
