package com.shoppingcart.services;


import com.shoppingcart.dtos.CancelOrderDto;
import com.shoppingcart.dtos.CartDetailsRequestDto;
import com.shoppingcart.dtos.CheckoutDto;
import com.shoppingcart.dtos.ReOrderOrderDto;
import com.shoppingcart.entities.Cart;
import com.shoppingcart.entities.CartItems;
import com.shoppingcart.entities.Orders;
import com.shoppingcart.entities.Products;
import com.shoppingcart.repositories.CartItemsRepository;
import com.shoppingcart.repositories.CartRepository;
import com.shoppingcart.repositories.OrdersRepository;
import com.shoppingcart.repositories.ProductsRepository;
import com.shoppingcart.utils.OrderStatus;
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
public class OrderService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private CartService cartService;

    /**
     * @param checkoutDto
     * @return
     */
    public ResponseModel checkOut(CheckoutDto checkoutDto) {

        //check if user has an active
        Cart cart = cartRepository.findFirstByUserIdAndStatus(checkoutDto.getUserId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            return new ResponseModel("failed", "Cart not found");

        List<CartItems> cartItems = cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString());
        //check if cart is empty
        if (cartItems.size() < 1)
            return new ResponseModel("failed", "Empty cart");

        //check if products in car are active and have suffient

        AtomicReference<Boolean> verifyCartProducts = new AtomicReference<>(true);
        AtomicReference<String> verificationProduct = new AtomicReference<>("Products validation");
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItem -> {
            //validate if product exists
            Optional<Products> productsOptional = productsRepository.findById(cartItem.getProductId());
            if (productsOptional.isEmpty()) {
                verifyCartProducts.set(false);
                verificationProduct.set("Product validation failed");
                return;
            }

            Products product = productsOptional.get();
            //check if stock exceed the quantity being bought
            if (product.getStockQuantity() < cartItem.getQuantity() || !product.getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())) {
                verifyCartProducts.set(false);
                verificationProduct.set("Product validation failed");
                return;
            }

            totalAmount.set(cartItem.getPrice().add(totalAmount.get()));
        });

        if (!verifyCartProducts.get()) {
            return new ResponseModel("failed", verificationProduct.get());
        }

        //Create order

        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.PAID.toString());
        order.setCartId(cart.getId());
        order.setUserId(checkoutDto.getUserId());
        order.setPaymentMethod(checkoutDto.getPaymentMethod());
        order.setItemsCount(cartItems.size());
        order.setTotalAmount(totalAmount.get());
        order.setStatus(RecordStatus.ACTIVE.toString());
        order.setUpdatedAt(new Date());
        order.setCreatedAt(new Date());
        ordersRepository.save(order);

        // Update inventory
        updateProductsQuantity(cartItems);
        //update cart
        cart.setStatus(RecordStatus.PROCESSED.toString());
        cartRepository.save(cart);

        return new ResponseModel("success", "Order placed successfully");
    }


    /**
     *
     * @param cartItems
     * @return
     */
    public List<Products> updateProductsQuantity(List<CartItems> cartItems) {
        List<Products> products = new ArrayList<>();
        cartItems.forEach(cartItem -> {
            //validate if product exists
            Products product = productsRepository.findById(cartItem.getProductId()).get();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            products.add(product);
        });
        productsRepository.saveAll(products);
        return products;
    }


    /**
     * @return
     */
    public ResponseModel cancelOrder(CancelOrderDto cancelOrderDto) {
        //check if orders  for a user if
        Orders order = ordersRepository.findFirstByIdAndUserId(cancelOrderDto.getOrderId(), cancelOrderDto.getUserId());
        if (null == order) {
            return new ResponseModel("failed", "Order not found");
        }
        if (!order.getOrderStatus().equalsIgnoreCase(OrderStatus.PAID.toString())) {
            return new ResponseModel("failed", "Sorry you are not allowed  to cancel this order at the moment");
        }

        order.setOrderStatus(OrderStatus.CANCELLED.toString());
        order.setStatus(OrderStatus.CANCELLED.toString());
        order.setComment(cancelOrderDto.getReason());
        ordersRepository.save(order);

        return new ResponseModel("success", "Order cancel successfully");
    }


    /**
     * @param reOrderOrderDto
     * @return
     */
    public Object reorderOrder(ReOrderOrderDto reOrderOrderDto) {
        Orders order = ordersRepository.findFirstByIdAndUserId(reOrderOrderDto.getOrderId(), reOrderOrderDto.getUserId());
        if (null == order) {
            return new ResponseModel("failed", "Order not found");
        }
        //Get user active cart else create one

        //Check if customer have active cart and update else create
        Cart cart1 = cartRepository.findFirstByUserIdAndStatus(reOrderOrderDto.getUserId(), RecordStatus.ACTIVE.toString());
        if (null != cart1) {
            cart1.setStatus(RecordStatus.DELETED.toString());
            cartRepository.save(cart1);
        }

        Cart cart = new Cart();
        cart.setItemsCount(0);
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setStatus(RecordStatus.ACTIVE.toString());
        cart.setUserId(reOrderOrderDto.getUserId());
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        cart = cartRepository.save(cart);
        Long cartId = cart.getId();
        // get items
        List<CartItems> cartItems = cartItemsRepository.findAllByCartIdAndStatus(order.getCartId(), RecordStatus.ACTIVE.toString());


        List<CartItems> newCartItems = new ArrayList<>();

        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItems1 -> {
            //check if product still exists and new pricing
            Optional<Products> products = productsRepository.findById(cartItems1.getProductId());
            if (products.isEmpty()) {
                return;
            }
            Products product = products.get();
            if (product.getStockQuantity() < cartItems1.getQuantity() || !product.getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())) {
                return;
            }
            CartItems cartItem = new CartItems();
            cartItem.setCartId(cartId);
            cartItem.setProductId(cartItems1.getProductId());
            cartItem.setPrice(product.getPrice());
            cartItem.setDiscountPercent(product.getDiscountPercent());
            cartItem.setQuantity(cartItems1.getQuantity());
            cartItem.setStatus(RecordStatus.ACTIVE.toString());
            cartItem.setCreatedAt(new Date());
            cartItem.setUpdatedAt(new Date());
            newCartItems.add(cartItem);

            total.set(total.get().add(product.getPrice()));
        });

        cartItemsRepository.saveAll(newCartItems);
        cart.setTotalAmount(total.get());
        cart.setItemsCount(newCartItems.size());
        cart = cartRepository.save(cart);
        CartDetailsRequestDto cartDetailsRequestDto=new CartDetailsRequestDto();
        cartDetailsRequestDto.setUserId(reOrderOrderDto.getUserId());


        return new ResponseModel("success", "Reorder successfully");
    }

}
