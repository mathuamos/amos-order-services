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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@Transactional
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private AppFunctions appFunctions;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private RecipesRepository recipesRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;


    @Mock
    private OrderService orderService1;

    @Mock
    private CartService cartService;



    private Users user;
    private Cart cart;
    private List<CartItems> cartItems;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(user.getId());
        cart.setStatus(RecordStatus.ACTIVE.toString());

        cartItems = new ArrayList<>();
        CartItems cartItem1 = new CartItems();
        cartItem1.setId(1L);
        cartItem1.setCartId(cart.getId());
        cartItem1.setRecipeId(1L);
        cartItem1.setPilotesCount(2);
        cartItem1.setPricePerPilote(BigDecimal.TEN);
        cartItem1.setStatus(RecordStatus.ACTIVE.toString());
        cartItems.add(cartItem1);
    }

    @Test
    void createOrder_success() {
        // mock dependencies
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);
        when(cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cartItems);
        when(recipesRepository.findById(cartItems.get(0).getRecipeId())).thenReturn(Optional.of(new Recipes()));
        when(ordersRepository.save(any(Orders.class))).thenReturn(new Orders());

        // create request DTO
        CheckoutDto checkoutDto = new CheckoutDto();
        checkoutDto.setAddress("123 Main St.");
        checkoutDto.setPaymentMethod("Credit Card");

        // call method
        ResponseModel responseModel = orderService.createOrder(checkoutDto);

        // verify result
        assertEquals("success", responseModel.getStatus());
        assertEquals("Order placed successfully", responseModel.getMessage());

        // verify dependencies called
        verify(appFunctions, times(1)).getLoginUser();
        verify(cartRepository, times(1)).findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        verify(cartItemsRepository, times(1)).findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString());
        verify(recipesRepository, times(1)).findById(cartItems.get(0).getRecipeId());
        verify(ordersRepository, times(1)).save(any(Orders.class));
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    void testCreateOrderWhenCartNotFound() {
        // given
        CheckoutDto checkoutDto = new CheckoutDto();
        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(null);

        // when
        CustomExceptionNotFound exception = assertThrows(CustomExceptionNotFound.class, () -> orderService.createOrder(checkoutDto));

        // then
        assertEquals("Cart not found", exception.getMessage());
    }


    @Test
    void testCreateOrderWhenOrderIsBeingEdited() {
        // given
        CheckoutDto checkoutDto = new CheckoutDto();
        Users user = new Users();
        user.setId(1L);
        Cart cart = new Cart();
        cart.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);
        List<CartItems> cartItems = new ArrayList<>();
        cartItems.add(new CartItems());
        when(cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cartItems);
        Orders order = new Orders();
        order.setStatus(RecordStatus.EDITING.toString());
        when(ordersRepository.findFirstByCartIdAndStatus(cart.getId(), RecordStatus.EDITING.toString())).thenReturn(order);

        // when
        ResponseModel response = orderService.createOrder(checkoutDto);

        // then
        assertEquals("failed", response.getStatus());
        assertEquals("you have an order which is on editing mode", response.getMessage());
    }






    @Test
    public void testCancelOrder() {
        // create mock CancelOrderDto object
        CancelOrderDto cancelOrderDto = new CancelOrderDto();
        cancelOrderDto.setOrderId(1L);
        cancelOrderDto.setReason("Test Reason");

        // create mock Users object for login user
        Users user = new Users();
        user.setId(1L);

        // create mock Orders object to be cancelled
        Orders order = new Orders();
        order.setId(1L);
        order.setUserId(user.getId());
        order.setOrderStatus(OrderStatus.PAID.toString());
        order.setStatus(OrderStatus.PAID.toString());
        order.setCreatedAt(new Date());

        // mock appFunctions.getLoginUser() to return the mock user
        Mockito.when(appFunctions.getLoginUser()).thenReturn(user);

        // mock appFunctions.getSupportedOrderUpdateTime() to return a date in the past
        Mockito.when(appFunctions.getSupportedOrderUpdateTime()).thenReturn(Date.from(Instant.now().minus(Duration.ofMinutes(1))));

        // mock ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter() to return the mock order
        Date checkOrderWasCreatedAfterDate = appFunctions.getSupportedOrderUpdateTime();
        Mockito.when(ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(cancelOrderDto.getOrderId(), user.getId(), checkOrderWasCreatedAfterDate))
                .thenReturn(order);

        // call cancelOrder method and assert that it returns a successful ResponseModel
        ResponseModel response = orderService.cancelOrder(cancelOrderDto);
        assertEquals("success", response.getStatus());
        assertEquals("Order cancel successfully", response.getMessage());

        // assert that the order status was updated to cancelled
        assertEquals(OrderStatus.CANCELLED.toString(), order.getOrderStatus());
        assertEquals(OrderStatus.CANCELLED.toString(), order.getStatus());
        assertEquals(cancelOrderDto.getReason(), order.getComment());
    }








    @Test
    public void cancelOrder_orderNotFound_throwException() {
        // Arrange
        CancelOrderDto cancelOrderDto = new CancelOrderDto();
        cancelOrderDto.setOrderId(1L);

        Users user = new Users();
        user.setId(1L);

        when(appFunctions.getLoginUser()).thenReturn(user);
        when(appFunctions.getSupportedOrderUpdateTime()).thenReturn(new Date());
        when(ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(anyLong(), anyLong(), any(Date.class))).thenReturn(null);

        // Act & Assert
        CustomExceptionNotFound thrown =
                org.junit.jupiter.api.Assertions.assertThrows(CustomExceptionNotFound.class, () -> {
                    orderService.cancelOrder(cancelOrderDto);
                });

        org.junit.jupiter.api.Assertions.assertEquals("Order not found", thrown.getMessage());
        verify(ordersRepository, times(1)).findFirstByIdAndUserIdAndCreatedAtAfter(anyLong(), anyLong(), any(Date.class));
    }



    @Test
    void reactivateCart_shouldReactivateCart() {
        // Setup
        Users user = new Users();
        user.setId(1L);
        Mockito.when(appFunctions.getLoginUser()).thenReturn(user);

        Date supportedOrderUpdateTime = new Date();
        Mockito.when(appFunctions.getSupportedOrderUpdateTime()).thenReturn(supportedOrderUpdateTime);

        Cart cart = new Cart();
        cart.setStatus(RecordStatus.INACTIVE.toString());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);

        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.PAID.toString());
        order.setCartLink(cart);
        Mockito.when(ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(order);
        Mockito.when(ordersRepository.save(Mockito.any())).thenReturn(order);

        ReactivateDto reactivateDto = new ReactivateDto();
        reactivateDto.setOrderId(1L);

        // Execution
        ResponseEntity<?> response = orderService.reactivateCart(reactivateDto);

        // Verification
        Mockito.verify(appFunctions).getLoginUser();
        Mockito.verify(appFunctions).getSupportedOrderUpdateTime();
        Mockito.verify(cartRepository).save(Mockito.any());
        Mockito.verify(ordersRepository).findFirstByIdAndUserIdAndCreatedAtAfter(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verify(ordersRepository).save(Mockito.any());


    }





    @Test
    void testMyOrdersSuccess() {
        // Arrange
        Users user = new Users();
        user.setId(1L);
        List<Orders> orders = new ArrayList<>();
        Orders order1 = new Orders();
        Orders order2 = new Orders();
        orders.add(order1);
        orders.add(order2);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(ordersRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId())).thenReturn(orders);

        // Act
        ResponseEntity<?> response = orderService.myOrders();

        // Assert
        assert response.getStatusCode() == HttpStatus.OK;
        ResponseModel responseModel = (ResponseModel) response.getBody();
        assert responseModel.getStatus().equals("success");
        assert responseModel.getMessage().equals("success");
        assert responseModel.getData().equals(orders);
    }


    @Test
    void testMyOrdersWhenNotFound() {
        // given
        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(ordersRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId())).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<?> response = orderService.myOrders();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appFunctions, times(1)).getLoginUser();
        verify(ordersRepository, times(1)).findAllByUserIdOrderByUpdatedAtDesc(user.getId());
    }




    @Test
    void testUpdateOrderWhenCartNotFound() {
        // Arrange
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(cartRepository.findFirstByUserIdAndStatus(any(), any())).thenReturn(null);

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> orderService.updateOrder(new UpdateOrderDto()));
    }

    @Test
    void testUpdateOrderWhenCartIsEmpty() {
        // Arrange
        Users user = new Users();
        Cart cart = new Cart();
        cart.setStatus(RecordStatus.ACTIVE.toString());
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(any(), any())).thenReturn(cart);
        when(cartItemsRepository.findAllByCartIdAndStatus(any(), any())).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> orderService.updateOrder(new UpdateOrderDto()));
    }

    @Test
    void testUpdateOrderWhenProductNotFound() {
        // Arrange
        Users user = new Users();
        Cart cart = new Cart();
        cart.setStatus(RecordStatus.ACTIVE.toString());
        List<CartItems> cartItems = new ArrayList<>();
        CartItems cartItem = new CartItems();
        cartItem.setRecipeId(1L);
        cartItem.setPricePerPilote(BigDecimal.ONE);
        cartItem.setPilotesCount(1);
        cartItems.add(cartItem);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(any(), any())).thenReturn(cart);
        when(cartItemsRepository.findAllByCartIdAndStatus(any(), any())).thenReturn(cartItems);
        when(recipesRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> orderService.updateOrder(new UpdateOrderDto()));
    }



    @Test
    public void testSearchOrder() {
        // Arrange
        String searchKey = "John";

        Users user1 = new Users();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");

        List<Users> users = Arrays.asList(user1, user2);
        when(userRepository.findAllByFirstNameContainsOrLastNameContains(searchKey, searchKey)).thenReturn(users);

        Orders order1 = new Orders();
        order1.setId(1L);
        order1.setUserLink(user1);
        order1.setStatus("Pending");

        Orders order2 = new Orders();
        order2.setId(2L);
        order2.setUserLink(user1);
        order2.setStatus("Delivered");

        Orders order3 = new Orders();
        order3.setId(3L);
        order3.setUserLink(user2);
        order3.setStatus("Processing");

        List<Orders> orders = Arrays.asList(order1, order2);
        when(ordersRepository.findAllByUserLinkIn(users)).thenReturn(orders);

        // Act
        ResponseEntity<?> responseEntity = orderService.searchOrder(searchKey);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(ResponseModel.class);
        ResponseModel responseModel = (ResponseModel) responseEntity.getBody();
        assertThat(responseModel.getStatus()).isEqualTo("success");
        assertThat(responseModel.getMessage()).isEqualTo("success");
        List<Orders> responseOrders = (List<Orders>) responseModel.getData();
        assertThat(responseOrders).containsExactlyInAnyOrder(order1, order2);
    }


    @Test
    void testSearchOrderWhenNotFound() {
        // given
        String searchKey = "nonexistent";
        when(userRepository.findAllByFirstNameContainsOrLastNameContains(searchKey, searchKey)).thenReturn(Collections.emptyList());
        when(ordersRepository.findAllByUserLinkIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<?> response = orderService.searchOrder(searchKey);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findAllByFirstNameContainsOrLastNameContains(searchKey, searchKey);
        verify(ordersRepository, times(1)).findAllByUserLinkIn(Collections.emptyList());
    }
}