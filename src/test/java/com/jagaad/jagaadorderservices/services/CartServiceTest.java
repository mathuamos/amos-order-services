package com.jagaad.jagaadorderservices.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jagaad.jagaadorderservices.configs.ApplicationProperties;
import com.jagaad.jagaadorderservices.dtos.AddToCartDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class CartServiceTest {

    @Mock
    private AppFunctions appFunctions;

    @Mock
    private RecipesRepository recipesRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private ApplicationProperties applicationProperties;




    @InjectMocks
    private CartService cartService;

    private Users user;
    private AddToCartDto addToCartDto;
    private Recipes recipe;

    @BeforeEach
    void setUp() {


        user = new Users();
        user.setId(1L);

        addToCartDto = new AddToCartDto();
        addToCartDto.setRecipeId(1L);
        addToCartDto.setNumberOfPilotes(2);

        recipe = new Recipes();
        recipe.setId(1L);
        recipe.setStatus(RecordStatus.ACTIVE.toString());
        recipe.setPrice(BigDecimal.TEN);



    }




    @Test
    @DisplayName("Test add to cart with recipe not available")
    void testAddToCartRecipeNotAvailable() {
        // Arrange
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(recipesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> cartService.addToCart(addToCartDto));
    }

    @Test
    @DisplayName("Test add to cart with recipe not active")
    void testAddToCartRecipeNotActive() {
        // Arrange
        recipe.setStatus(RecordStatus.INACTIVE.toString());
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(recipesRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> cartService.addToCart(addToCartDto));
    }

    @Test
    @DisplayName("Test add to cart with unsupported number of pilotes")
    void testAddToCartUnsupportedNumberOfPilotes() {
        // Arrange
        when(appFunctions.getLoginUser()).thenReturn(user);
        when(recipesRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(applicationProperties.getSupportedPilotsCount()).thenReturn(List.of(1L));

        // Act and Assert
        assertThrows(CustomExceptionNotFound.class, () -> cartService.addToCart(addToCartDto));
    }






}
