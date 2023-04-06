package com.jagaad.jagaadorderservices.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagaad.jagaadorderservices.configs.ApplicationProperties;
import com.jagaad.jagaadorderservices.dtos.AddToCartDto;

import com.jagaad.jagaadorderservices.dtos.ModifyCartItemDto;
import com.jagaad.jagaadorderservices.dtos.RemoveCartItemFromCartDto;
import com.jagaad.jagaadorderservices.entities.Cart;
import com.jagaad.jagaadorderservices.entities.CartItems;
import com.jagaad.jagaadorderservices.entities.Recipes;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.exceptions.CustomExceptionNotFound;
import com.jagaad.jagaadorderservices.repositories.CartItemsRepository;
import com.jagaad.jagaadorderservices.repositories.CartRepository;
import com.jagaad.jagaadorderservices.repositories.RecipesRepository;
import com.jagaad.jagaadorderservices.utils.AppFunctions;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import com.jagaad.jagaadorderservices.utils.ResponseModel;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class CartUnitTests {

    @Mock
    private RecipesRepository recipesRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;




    @Mock
    private ApplicationProperties applicationProperties;



    private ObjectMapper objectMapper;

    @Mock
    private AppFunctions appFunctions;

    @Captor
    private ArgumentCaptor<Cart> cartArgumentCaptor;

    @Captor
    private ArgumentCaptor<CartItems> cartItemsArgumentCaptor;

    @Autowired
    private CartService cartService;

    private final Long userId = 1L;

    private Users loginUser;

    private Cart cart;

    private CartItems cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(
                recipesRepository,
                cartRepository,
                cartItemsRepository,
                applicationProperties,
                appFunctions
        );

        loginUser = new Users();
        loginUser.setId(1L);

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(loginUser.getId());
        cart.setStatus(RecordStatus.ACTIVE.toString());

        cartItem = new CartItems();
        cartItem.setId(1L);
        cartItem.setCartId(cart.getId());
        cartItem.setRecipeLink(new Recipes());
        cartItem.setStatus(RecordStatus.ACTIVE.toString());
        cartItem.setTotalAmount(BigDecimal.TEN);
        cartItem.setPilotesCount(2);
    }

    @Test
    void addToCart_withValidInput_shouldReturnSuccessResponse() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setRecipeId(1L);
        addToCartDto.setNumberOfPilotes(2);

        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);

        Recipes recipe = new Recipes();
        recipe.setId(1L);
        recipe.setStatus(RecordStatus.ACTIVE.toString());
        recipe.setPrice(new BigDecimal("10"));
        when(recipesRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(user.getId());
        cart.setStatus(RecordStatus.ACTIVE.toString());
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);

        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        when(cartItemsRepository.save(any())).thenReturn(cartItem);

        testAddToCart_InvalidPilotsCount();
        testAddToCart_ActiveCartExists();
        // Act
        ResponseModel response = cartService.addToCart(addToCartDto);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Recipe added to cart", response.getMessage());
        assertEquals(cartItem, response.getData());
        verify(appFunctions,times(3)).getLoginUser();
        verify(recipesRepository,times(3)).findById(1L);
    }

    @Test
    void addToCart_withInvalidRecipe_shouldThrowCustomExceptionNotFound() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setRecipeId(1L);
        addToCartDto.setNumberOfPilotes(2);

        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);

        when(recipesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> cartService.addToCart(addToCartDto));
        verify(appFunctions, times(1)).getLoginUser();
        verify(recipesRepository, times(1)).findById(1L);
        verify(cartRepository, never()).findFirstByUserIdAndStatus(anyLong(), anyString());
        verify(cartRepository, never()).save(any());
        verify(cartItemsRepository, never()).save(any());
    }

    @Test
    void addToCart_withInactiveRecipe_shouldThrowCustomExceptionNotFound() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setRecipeId(1L);
        addToCartDto.setNumberOfPilotes(2);

        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);

        Recipes recipe = new Recipes();
        recipe.setId(1L);
        recipe.setStatus(RecordStatus.INACTIVE.toString());
        when(recipesRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> cartService.addToCart(addToCartDto));
        verify(appFunctions, times(1)).getLoginUser();
        verify(recipesRepository, times(1)).findById(1L);
        verify(cartRepository, never()).findFirstByUserIdAndStatus(anyLong(), anyString());
        verify(cartRepository, never()).save(any());
        verify(cartItemsRepository, never()).save(any());
    }


    @Test
    void testAddToCart_RecipeNotFound() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(9999L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
    }

    @Test
    void testAddToCart_RecipeNotActive() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        Recipes recipe = new Recipes();
        recipe.setStatus(RecordStatus.DELETED.toString());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(recipe));

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
    }

    @Test
    void testAddToCart_InvalidPilotsCount() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 3);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(new Recipes()));
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(List.of(1L, 2L));

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
    }

    @Test
    void testAddToCart_NoActiveCart() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(new Recipes()));
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(List.of(1L, 2L));
        when(cartRepository.findFirstByUserIdAndStatus(anyLong(), anyString())).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());

        // Act
        ResponseModel response = cartService.addToCart(addToCartDto);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Recipe added to cart", response.getMessage());
    }

    @Test
    void testAddToCart_ActiveCartExists() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(new Recipes()));
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(List.of(1L, 2L));
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setRecipesCount(2);
        cart.setStatus(RecordStatus.ACTIVE.toString());
        when(cartRepository.findFirstByUserIdAndStatus(anyLong(), anyString())).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        ResponseModel response = cartService.addToCart(addToCartDto);

        // Assert
        assertNotNull(response);
        // assertEquals("success
    }

    @Test
    void addToCart_shouldThrowCustomExceptionNotFound_whenRecipeNotFound() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1234L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
        verify(cartRepository, never()).findFirstByUserIdAndStatus(anyLong(), anyString());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemsRepository, never()).save(any(CartItems.class));
    }

    @Test
    void addToCart_shouldThrowCustomExceptionNotFound_whenRecipeInactive() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1234L, 2);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        Recipes inactiveRecipe = new Recipes();
        inactiveRecipe.setId(addToCartDto.getRecipeId());
        inactiveRecipe.setStatus(RecordStatus.INACTIVE.toString());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(inactiveRecipe));

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
        verify(cartRepository, never()).findFirstByUserIdAndStatus(anyLong(), anyString());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemsRepository, never()).save(any(CartItems.class));
    }

    @Test
    void addToCart_shouldThrowCustomExceptionNotFound_whenNoOfPilotesNotSupported() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1234L, 4);
        when(appFunctions.getLoginUser()).thenReturn(new Users());
        Recipes recipe = new Recipes();
        recipe.setId(addToCartDto.getRecipeId());
        recipe.setStatus(RecordStatus.ACTIVE.toString());
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(recipe));
        List<Long> supportedPilotsCount = Arrays.asList(1L, 2L, 3L);
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(supportedPilotsCount);

        // Act & Assert
        assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
        verify(cartRepository, never()).findFirstByUserIdAndStatus(anyLong(), anyString());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemsRepository, never()).save(any(CartItems.class));
    }

    @Test
    void addToCart_shouldCreateNewCartAndCartItem_whenUserHasNoActiveCart() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1234L, 2);
        Users user = new Users();
        user.setId(123L);
        when(appFunctions.getLoginUser()).thenReturn(user);
        Recipes recipe = new Recipes();
        recipe.setId(addToCartDto.getRecipeId());
        recipe.setStatus(RecordStatus.ACTIVE.toString());
        recipe.setPrice(BigDecimal.valueOf(10));
        when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(Optional.of(recipe));
        List<Long> supportedPilotsCount = Arrays.asList(1L, 2L, 3L);
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(supportedPilotsCount);
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    }


    @Test
    void addToCart_recipeNotAvailable_throwException() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setRecipeId(1L);
        Users user = new Users();
        Mockito.when(appFunctions.getLoginUser()).thenReturn(user);
        Optional<Recipes> recipe = Optional.empty();
        Mockito.when(recipesRepository.findById(addToCartDto.getRecipeId())).thenReturn(recipe);

        // Act and Assert
        CustomExceptionNotFound thrown = assertThrows(CustomExceptionNotFound.class, () -> {
            cartService.addToCart(addToCartDto);
        });
        assertEquals("Recipe not available", thrown.getMessage());
        verify(appFunctions).getLoginUser();
        verify(recipesRepository).findById(addToCartDto.getRecipeId());
    }


    @Test
    public void testCartDetails() {
        // Mock login user
        Users user = new Users();
        user.setId(1L);
        user.setFirstName("testuser");
        when(appFunctions.getLoginUser()).thenReturn(user);

        // Mock cart repository
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(user.getId());
        cart.setStatus(RecordStatus.ACTIVE.toString());
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);

        // Call the method
        ResponseModel response = cartService.cartDetails();

        // Verify the response
        assertEquals("success", response.getStatus());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getData());
    }


    @Test
    public void testRecipes() {
        // Arrange
        RecipesRepository recipesRepository = mock(RecipesRepository.class);
        Iterable<Recipes> expectedRecipes = createSampleRecipes();
        when(recipesRepository.findAll()).thenReturn(expectedRecipes);



        // Act
        ResponseEntity<?> response = cartService.recipes();
        ResponseModel responseModel = (ResponseModel) response.getBody();

        // Assert
        assertEquals("success", responseModel.getStatus());
        assertEquals("success", responseModel.getMessage());

    }

    private Iterable<Recipes> createSampleRecipes() {
        List<Recipes> recipes = new ArrayList<>();
        recipes.add(new Recipes("Pasta", "Italian", new BigDecimal(1.33),5));
        recipes.add(new Recipes("Biryani", "Indian", new BigDecimal(1.33),5));
        return recipes;
    }

    @Test()
    @Disabled
    public void testRemoveProductFromCart() {
        // Set up input data
        RemoveCartItemFromCartDto removeCartItemFromCartDto = new RemoveCartItemFromCartDto();
        removeCartItemFromCartDto.setCartItemId(1L);
        removeCartItemFromCartDto.setCartId(1L);

        // Set up mock repository method calls
        Users user = new Users();
        user.setId(1L);

        when(appFunctions.getLoginUser()).thenReturn(user);

        Cart cart = new Cart();
        cart.setId(1L);

        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);
        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        when(cartItemsRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemsRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemsRepository.countAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString())).thenReturn(1);

        // Call the method being tested
        Object result = cartService.removeProductFromCart(removeCartItemFromCartDto);


        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ResponseModel.class);
        assertThat(((ResponseModel) result).getMessage()).isEqualTo("success");

        // Verify that the cart item was marked as deleted and saved
        assertThat(cartItem.getStatus()).isEqualTo(RecordStatus.DELETED.toString());
        assertThat(cartItemsRepository.save(cartItem)).isEqualTo(cartItem);
    }

    @Test
    public void testRemoveProductFromCartWithInvalidCart() {
        // Set up input data
        RemoveCartItemFromCartDto removeCartItemFromCartDto = new RemoveCartItemFromCartDto();
        removeCartItemFromCartDto.setCartItemId(1L);
        removeCartItemFromCartDto.setCartId(1L);

        // Set up mock repository method calls
        Users user = new Users();
        user.setId(1L);
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(null);

        // Call the method being tested and verify that it throws the expected exception
        assertThrows(CustomExceptionNotFound.class, () -> cartService.removeProductFromCart(removeCartItemFromCartDto));
    }


    @Test
    public void testRemoveProductFromCartFunction() {
;


        Users user = new Users();
        user.setId(1L);

        when(appFunctions.getLoginUser()).thenReturn(user);

        // Mock cart repository
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(user.getId());
        cart.setStatus(RecordStatus.ACTIVE.toString());
        when(cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString())).thenReturn(cart);


        // Set up mock behavior
        when(cartItemsRepository.countAllByCartIdAndStatus(1L, RecordStatus.ACTIVE.toString()))
                .thenReturn(1);
        when(cartItemsRepository.findFirstByIdAndCartIdAndStatus(1L, 1L, RecordStatus.ACTIVE.toString()))
                .thenReturn(cartItem);


        // Call the method to be tested
       cartService.removeProductFromCartFunction(cart, 1L);

        // Verify that the repository methods were called with the expected parameters
        verify(cartItemsRepository).countAllByCartIdAndStatus(1L, RecordStatus.ACTIVE.toString());
        verify(cartItemsRepository).findFirstByIdAndCartIdAndStatus(1L, 1L, RecordStatus.ACTIVE.toString());
       verify(cartItemsRepository).save(cartItem);



    }









}
