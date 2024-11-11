package app.backend.click_and_buy.repositories;

import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);
    CartItem findCartItemByCartItemId(Long cartItemId);

    long countCartItemByCart(Cart cart);
}
