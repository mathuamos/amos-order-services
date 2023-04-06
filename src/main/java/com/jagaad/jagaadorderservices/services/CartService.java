package com.shoppingcart.services;


import com.shoppingcart.dtos.AddToCartDto;
import com.shoppingcart.dtos.CartDetailsRequestDto;
import com.shoppingcart.dtos.CartDetailsResponseDto;
import com.shoppingcart.dtos.RemoveProductFromCartDto;
import com.shoppingcart.entities.Cart;
import com.shoppingcart.entities.CartItems;
import com.shoppingcart.entities.Products;
import com.shoppingcart.repositories.CartItemsRepository;
import com.shoppingcart.repositories.CartRepository;
import com.shoppingcart.repositories.ProductsRepository;
import com.shoppingcart.utils.RecordStatus;
import com.shoppingcart.utils.ResponseModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@Log4j2
public class CartService {

    @Autowired
    ProductsRepository productsRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;

    public ResponseModel addToCart(AddToCartDto addToCartDto) {
        Optional<Products> products = productsRepository.findById(addToCartDto.getProductId());
        if (products.isEmpty()){
            return new ResponseModel("error","Product not available");
        }
        Products product=products.get();
        if (product.getStockQuantity()<addToCartDto.getQuantity() || !product.getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())){
            return new ResponseModel("error","Product not available at the moment");
        }

        //Check if customer have active cart and update else create
        Cart cart=cartRepository.findFirstByUserIdAndStatus(addToCartDto.getUserId(), RecordStatus.ACTIVE.toString());
        if (null==cart){
            cart=new Cart();
            cart.setItemsCount(addToCartDto.getQuantity());
            cart.setTotalAmount(product.getPrice());
            cart.setStatus(RecordStatus.ACTIVE.toString());
            cart.setUserId(addToCartDto.getUserId());
            cart.setCreatedAt(new Date());
        }
        else {
            cart.setItemsCount(cart.getItemsCount() + addToCartDto.getQuantity());
            cart.setTotalAmount(cart.getTotalAmount().add(product.getPrice()));
        }
        cart.setUpdatedAt(new Date());
        cart=cartRepository.save(cart);

        //Product quantity is less than on remove products from cat
        if (addToCartDto.getQuantity()<1){
            removeProductFromCart(cart.getId(),product.getId());
            return new ResponseModel("success","Product added to cart");

        }
        CartItems cartItem= cartItemsRepository.findFirstByCartIdAndProductIdAndStatus(cart.getId(), product.getId(),RecordStatus.ACTIVE.toString());
        if( null==cartItem){
            cartItem=new CartItems();
            cartItem.setCartId(cart.getId());
            cartItem.setProductId(product.getId());
            cartItem.setPrice(product.getPrice());
            cartItem.setDiscountPercent(product.getDiscountPercent());
            cartItem.setQuantity(addToCartDto.getQuantity());
            cartItem.setStatus(RecordStatus.ACTIVE.toString());
            cartItem.setCreatedAt(new Date());
        }else {
            cartItem.setPrice(product.getPrice());
            cartItem.setDiscountPercent(product.getDiscountPercent());
            cartItem.setQuantity(addToCartDto.getQuantity());
        }
        cartItem.setUpdatedAt(new Date());
        cartItem=cartItemsRepository.save(cartItem);
        return new ResponseModel("success","Product added to cart",cartItem);
    }


    public Object  removeProductFromCart(RemoveProductFromCartDto removeProductFromCartDto){
        //check if user has an active
        Cart cart = cartRepository.findFirstByUserIdAndStatus(removeProductFromCartDto.getUserId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            return new ResponseModel("failed", "Cart not found");
        // Method to remove product from cart
        removeProductFromCart(cart.getUserId(),removeProductFromCartDto.getProductId());

        return  new ResponseModel("success","Item removed successfully");
    }

    /**
     * Method to remove product from cart
     * @param cartId
     * @param product
     */
    public void  removeProductFromCart(Long cartId, Long product ){
        CartItems cartItem= cartItemsRepository.findFirstByCartIdAndProductIdAndStatus(cartId, product,RecordStatus.ACTIVE.toString());
        cartItem.setStatus(RecordStatus.DELETED.toString());
        cartItemsRepository.save(cartItem);
    }

    public  ResponseModel cartDetails(CartDetailsRequestDto cartDetailsRequestDto){
        Cart cart = cartRepository.findFirstByUserIdAndStatus(cartDetailsRequestDto.getUserId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            return new ResponseModel("failed", "Cart not found");
        List<CartItems> cartItems= cartItemsRepository.findAllByCartIdAndStatus(cart.getId(),RecordStatus.ACTIVE.toString());
        CartDetailsResponseDto cartDetailsResponseDto=new CartDetailsResponseDto();
        List<CartDetailsResponseDto.ItemDetails> itemDetails=new ArrayList<>();

        AtomicReference<BigDecimal> total= new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalDiscount= new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItem -> {
            CartDetailsResponseDto.ItemDetails itemDetail=new CartDetailsResponseDto.ItemDetails();
            itemDetail.setDiscountPercent(cartItem.getDiscountPercent());
            itemDetail.setProductName(cartItem.getProductsLink() !=null ? cartItem.getProductsLink().getName() :"");
            itemDetail.setPrice(cartItem.getPrice());
            itemDetail.setQuantity(cartItem.getQuantity());
            itemDetail.setProductId(cartItem.getProductId());
            try {
                itemDetail.setDiscountAmount(itemDetail.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())).multiply(BigDecimal.valueOf(cartItem.getDiscountPercent())));
            }catch (Exception e){
                itemDetail.setDiscountAmount(BigDecimal.ZERO);
            }
            itemDetails.add(itemDetail);
            total.set( total.get().add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))));
            totalDiscount.set(totalDiscount.get().add(itemDetail.getDiscountAmount()));
        });
        cartDetailsResponseDto.setItemDetails(itemDetails);
        cartDetailsResponseDto.setTotal(total.get());
        cartDetailsResponseDto.setTotalDiscount(totalDiscount.get());

        return new ResponseModel("success","success",cartDetailsResponseDto);

    }
}
