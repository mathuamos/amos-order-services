package com.jagaad.jagaadorderservices.services;


import com.jagaad.jagaadorderservices.dtos.CancelOrderDto;
import com.jagaad.jagaadorderservices.dtos.CheckoutDto;
import com.jagaad.jagaadorderservices.dtos.ReactivateDto;
import com.jagaad.jagaadorderservices.dtos.UpdateOrderDto;
import com.jagaad.jagaadorderservices.entities.*;
import com.jagaad.jagaadorderservices.exceptions.CustomExceptionNotFound;
import com.jagaad.jagaadorderservices.repositories.*;
import com.jagaad.jagaadorderservices.utils.AppFunctions;
import com.jagaad.jagaadorderservices.utils.OrderStatus;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import com.jagaad.jagaadorderservices.utils.ResponseModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@Log4j2
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final RecipesRepository recipesRepository;
    private final OrdersRepository ordersRepository;
    private final CartService cartService;
    private final AppFunctions appFunctions;
    private final UserRepository userRepository;


    public OrderService(CartRepository cartRepository, CartItemsRepository cartItemsRepository, RecipesRepository recipesRepository, OrdersRepository ordersRepository, CartService cartService, AppFunctions appFunctions, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.recipesRepository = recipesRepository;
        this.ordersRepository = ordersRepository;
        this.cartService = cartService;
        this.appFunctions = appFunctions;
        this.userRepository = userRepository;
    }

    /**
     * method to create order
     */
    public ResponseModel createOrder(CheckoutDto checkoutDto) {



        //get login in user
        Users user = appFunctions.getLoginUser();



        //check if user has an active
        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null == cart)

            throw new CustomExceptionNotFound("Cart not found");

        List<CartItems> cartItems = cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString());
        //check if cart is empty
        if (cartItems.size() < 1)
            return new ResponseModel("failed", "Empty cart");




        //check if user is editing order
        Orders orderchecker =ordersRepository.findFirstByCartIdAndStatus(cart.getId(),RecordStatus.EDITING.toString());

        if (orderchecker != null) {
            return new ResponseModel("failed", "you have an order which is on editing mode");
        }


        //check if products in car are active

        AtomicReference<Boolean> verifyCartProducts = new AtomicReference<>(true);
        AtomicReference<String> verificationProduct = new AtomicReference<>("Products validation");
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItem -> {
            //validate if recipe exists
            Optional<Recipes> productsOptional = recipesRepository.findById(cartItem.getRecipeId());
            if (productsOptional.isEmpty()) {
                verifyCartProducts.set(false);
                verificationProduct.set("Recipe validation failed");
                return;
            }

            Recipes recipes = productsOptional.get();
            //check if recipe is active
            if (!recipes.getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())) {
                verifyCartProducts.set(false);
                verificationProduct.set("Product validation failed");
                return;
            }

            totalAmount.set(cartItem.getPricePerPilote().multiply(new BigDecimal(cartItem.getPilotesCount())).add(totalAmount.get()));
        });

        if (!verifyCartProducts.get()) {
            return new ResponseModel("failed", verificationProduct.get());
        }

        //Create order

        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.PAID.toString());
        order.setCartId(cart.getId());
        order.setUserId(user.getId());
        order.setPaymentMethod(checkoutDto.getPaymentMethod());
        order.setItemsCount(cartItems.size());
        order.setTotalAmount(totalAmount.get());
        order.setStatus(RecordStatus.ACTIVE.toString());
        order.setUpdatedAt(new Date());
        order.setCreatedAt(new Date());
        order.setAddress(checkoutDto.getAddress());
        order.setComment(checkoutDto.getComment());
        ordersRepository.save(order);


        //update cart
        cart.setStatus(RecordStatus.PROCESSED.toString());
        cartRepository.save(cart);

        return new ResponseModel("success", "Order placed successfully");
    }


    /**
     * method to cancel order
     */
        public ResponseModel cancelOrder(CancelOrderDto cancelOrderDto) {
    
            //get login in user
            Users user = appFunctions.getLoginUser();
    
    
            //check if orders  for a user if
            Date checkOrderWasCreatedAfterDate = appFunctions.getSupportedOrderUpdateTime();
            Orders order = ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(cancelOrderDto.getOrderId(), user.getId(), checkOrderWasCreatedAfterDate);
            if (null == order) {
                throw new CustomExceptionNotFound("Order not found");
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

    public ResponseEntity<?> reactivateCart(ReactivateDto reactivateDto) {


        //get login in user
        Users user = appFunctions.getLoginUser();

        Date checkOrderWasCreatedAfterDate = appFunctions.getSupportedOrderUpdateTime();
        Orders order = ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(reactivateDto.getOrderId(), user.getId(), checkOrderWasCreatedAfterDate);
        if (null == order) {
            ResponseEntity.badRequest().body(new ResponseModel("failed", "Order not found"));
        }
        assert order != null;
        if (!order.getOrderStatus().equalsIgnoreCase(OrderStatus.PAID.toString())) {
            ResponseEntity.badRequest().body(new ResponseModel("failed", "Sorry you are not allowed  to cancel this order at the moment"));
        }

        Cart cart = order.getCartLink();
        cart.setStatus(RecordStatus.ACTIVE.toString());
        cart.setUpdatedAt(new Date());
        cartRepository.save(cart);

        order.setStatus(RecordStatus.EDITING.toString());
        order.setUpdatedAt(new Date());
        ordersRepository.save(order);

        return ResponseEntity.ok(new ResponseModel("success", "Cart reactivated", cartService.getPreparedCarDetails(cart)));
    }

    public ResponseEntity<?> myOrders() {
        Users user = appFunctions.getLoginUser();

        log.info("User  in session {} email {} id {}", user.getFirstName(),user.getEmail(),user.getId());
        List<Orders> orders = ordersRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId());
        return ResponseEntity.ok(new ResponseModel("success", "success", orders));
    }






    public ResponseModel updateOrder(UpdateOrderDto updateOrderDto) {


        Users user = appFunctions.getLoginUser();
        log.info("User  in session {} email {} id {}", user.getFirstName(),user.getEmail(),user.getId());

        //check if user has an active
        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
//            return new ResponseModel("failed", "Cart not found");
            throw new CustomExceptionNotFound("Cart not found");

        List<CartItems> cartItems = cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString());
        //check if cart is empty
        if (cartItems.size() < 1)
            throw new CustomExceptionNotFound("Cart is Empty");

        //check if products in car are active

        AtomicReference<Boolean> verifyCartProducts = new AtomicReference<>(true);
        AtomicReference<String> verificationProduct = new AtomicReference<>("Products validation");
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItem -> {
            //validate if product exists
            Optional<Recipes> productsOptional = recipesRepository.findById(cartItem.getRecipeId());
            if (productsOptional.isEmpty()) {
                verifyCartProducts.set(false);
                verificationProduct.set("Product validation failed");
                return;
            }

            Recipes recipes = productsOptional.get();
            //check if stock exceed the quantity being bought
            if (!recipes.getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())) {
                verifyCartProducts.set(false);
                verificationProduct.set("Product validation failed");
                return;
            }

            totalAmount.set(cartItem.getPricePerPilote().multiply(new BigDecimal(cartItem.getPilotesCount())).add(totalAmount.get()));
        });

        if (!verifyCartProducts.get()) {
            throw new CustomExceptionNotFound("item not valid "+verificationProduct.get());

        }

        //get order and check if its in editing mode

        Orders order =ordersRepository.findFirstByIdAndUserIdAndStatus(updateOrderDto.getOrderId(),user.getId(),RecordStatus.EDITING.toString());

        if (order == null) {
            return new ResponseModel("failed", "order not found or not in editing mode");

        }

        //update order

        order.setOrderStatus(OrderStatus.PAID.toString());
        order.setCartId(cart.getId());
        order.setUserId(user.getId());
        order.setPaymentMethod(updateOrderDto.getPaymentMethod());
        order.setItemsCount(cartItems.size());
        order.setTotalAmount(totalAmount.get());
        order.setStatus(RecordStatus.ACTIVE.toString());
        order.setUpdatedAt(new Date());
        order.setAddress(updateOrderDto.getAddress());
        order.setComment(updateOrderDto.getComment());
        ordersRepository.save(order);

        //update cart
        cart.setStatus(RecordStatus.PROCESSED.toString());
        cartRepository.save(cart);

        return new ResponseModel("success", "Order updated successfully");
    }



    public ResponseEntity<?> searchOrder(String searchKey){

        List<Users> users=userRepository.findAllByFirstNameContainsOrLastNameContains(searchKey,searchKey);

        List<Orders> orders=ordersRepository.findAllByUserLinkIn(users);

        return ResponseEntity.ok(new ResponseModel("success","success",orders));

    }

}
