package com.jagaad.jagaadorderservices.controllers;

import com.jagaad.jagaadorderservices.dtos.CancelOrderDto;
import com.jagaad.jagaadorderservices.dtos.CheckoutDto;
import com.jagaad.jagaadorderservices.dtos.ReactivateDto;
import com.jagaad.jagaadorderservices.dtos.UpdateOrderDto;
import com.jagaad.jagaadorderservices.services.CartService;
import com.jagaad.jagaadorderservices.services.OrderService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
public class OrdersController {


    //this controller managers all orders operations



    private final OrderService orderService;
    private final CartService cartService;

    public OrdersController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;

    }


    @GetMapping("/api/v1/myorders")
    public ResponseEntity<?> myOrders(){
        return orderService.myOrders();
    }









    @PostMapping("/api/v1/update-order")
    public ResponseEntity<?> updateOrders(@RequestBody  @Valid  UpdateOrderDto updateOrderDto){
        return ResponseEntity.ok(orderService.updateOrder(updateOrderDto));
    }



    @PostMapping("/api/v1/cancel-order")
    public ResponseEntity<?> cancelOrders(@RequestBody @Valid  CancelOrderDto cancelOrderDto){
        return ResponseEntity.ok(orderService.cancelOrder(cancelOrderDto));
    }



    @PostMapping("/api/v1/checkout")
    public ResponseEntity<?> checkoutCart(@RequestBody  @Valid CheckoutDto checkoutCart){
        return ResponseEntity.ok(orderService.createOrder(checkoutCart));
    }


    @PostMapping("/api/v1/reactivate-cart")
    public ResponseEntity<?> reactivateCart(@RequestBody @Valid  ReactivateDto reactivateDto){
        return ResponseEntity.ok(orderService.reactivateCart(reactivateDto));
    }


    @GetMapping("/api/v1/search-order")
    public ResponseEntity<?> searchOrder(@Param("searchKey") String searchKey){
        return orderService.searchOrder(searchKey);
    }
}



