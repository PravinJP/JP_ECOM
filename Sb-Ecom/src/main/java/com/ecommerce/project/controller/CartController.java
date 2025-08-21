package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }
@GetMapping("/carts")
public ResponseEntity<List<CartDTO>>fetchAllCart(){
        List<CartDTO> cartDTO=cartService.getALlCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTO,HttpStatus.FOUND);
}

@GetMapping("/carts/users/cart")
public ResponseEntity<CartDTO>UsersCart(){
        String emailId=authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Long cartId= cart.getCartId();
        CartDTO cartDTO=cartService.getcarts(emailId,cartId);
return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.FOUND);
}

@PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO>updateCart(@PathVariable Long productId,@PathVariable String operation)
{
   CartDTO cartDTO= cartService.updateProductQuantity(productId,
           operation.equalsIgnoreCase("delete")? -1:1);
   return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
}
@DeleteMapping("/carts/{cartId}/products/{productId}")
    public ResponseEntity<String>DeleteProductFromCart(@PathVariable Long cartId,@PathVariable Long productId){
        String status=cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<String>(status,HttpStatus.OK);
}


}


