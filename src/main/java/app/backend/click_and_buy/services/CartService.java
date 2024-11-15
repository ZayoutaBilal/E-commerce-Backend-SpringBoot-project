package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class CartService {

    private  final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public Cart findCartByCustomerId(long id) {
        return cartRepository.findCartByCustomerCustomerId(id);
    }


}
