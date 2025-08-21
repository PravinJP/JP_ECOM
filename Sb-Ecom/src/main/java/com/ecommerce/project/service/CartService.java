package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity) ;

    List<CartDTO> getALlCarts();

    CartDTO getcarts(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantity(Long productId, int quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
