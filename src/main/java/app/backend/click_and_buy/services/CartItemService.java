package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.entities.CartItem;
import app.backend.click_and_buy.repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public void saveCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    public void updateCartItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    public List<CartItem> findCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    public void deleteCartItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public CartItem findCartItemById(Long id) {
        return cartItemRepository.findCartItemByCartItemId(id);
    }

    public long countCartItemByCart(Cart cart){
        return cartItemRepository.countCartItemByCart(cart);
    }

}
