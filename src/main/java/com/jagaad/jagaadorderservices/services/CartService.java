package com.jagaad.jagaadorderservices.services;


import com.jagaad.jagaadorderservices.configs.ApplicationProperties;
import com.jagaad.jagaadorderservices.dtos.AddToCartDto;
import com.jagaad.jagaadorderservices.dtos.CartDetailsResponseDto;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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

    private final RecipesRepository recipesRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ApplicationProperties applicationProperties;

    private final AppFunctions appFunctions;

    public CartService(RecipesRepository recipesRepository, CartRepository cartRepository, CartItemsRepository cartItemsRepository, ApplicationProperties applicationProperties, AppFunctions appFunctions) {
        this.recipesRepository = recipesRepository;
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.applicationProperties = applicationProperties;
        this.appFunctions = appFunctions;
    }


    public ResponseEntity<?> recipes() {

        Iterable<Recipes> recipes = recipesRepository.findAll();
        return ResponseEntity.ok(new ResponseModel("success", "success", recipes));
    }


    public ResponseModel addToCart(AddToCartDto addToCartDto) {

        //get login user
        Users user = appFunctions.getLoginUser();

        //check if the recipe exists
        Optional<Recipes> recipe = recipesRepository.findById(addToCartDto.getRecipeId());
        if (recipe.isEmpty()){
            //return new ResponseModel("error","Recipe not available");
            throw new CustomExceptionNotFound("Recipe not available");
        }

        //check if the recipe is active
        if (!recipe.get().getStatus().equalsIgnoreCase(RecordStatus.ACTIVE.toString())) {
            throw new CustomExceptionNotFound("Recipe not available at the moment");
        }

        //check if no of pilotes are supported
        if (applicationProperties.getSupportedPilotsCount().stream().noneMatch(conf->conf==addToCartDto.getNumberOfPilotes().intValue())){
            throw new CustomExceptionNotFound("No of pilotes is not in our list");
        }


        //Check if customer have active cart and update else create
        Cart cart=cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null==cart){
            cart=new Cart();
            cart.setRecipesCount(1);
            cart.setStatus(RecordStatus.ACTIVE.toString());
            cart.setUserId(user.getId());
            cart.setCreatedAt(new Date());
        }
        else {
            cart.setRecipesCount(cart.getRecipesCount() + 1);
        }
        cart.setUpdatedAt(new Date());
        cart=cartRepository.save(cart);

       //create new item

        Recipes recipes=recipe.get();
        CartItems cartItem =new CartItems();
        cartItem.setCartId(cart.getId());
        cartItem.setRecipeId(recipes.getId());
        cartItem.setPricePerPilote(recipes.getPrice() !=null ? recipes.getPrice(): BigDecimal.ZERO);
        cartItem.setTotalAmount(cartItem.getPricePerPilote().multiply(new BigDecimal(addToCartDto.getNumberOfPilotes())));
        cartItem.setPilotesCount(addToCartDto.getNumberOfPilotes());
        cartItem.setStatus(RecordStatus.ACTIVE.toString());
        cartItem.setCreatedAt(new Date());

        cartItem.setUpdatedAt(new Date());
        cartItem=cartItemsRepository.save(cartItem);
        return new ResponseModel("success","Recipe added to cart",cartItem);
    }


    public Object  removeProductFromCart(RemoveCartItemFromCartDto removeCartItemFromCartDto){
        //check if user has an active

        Users user = appFunctions.getLoginUser();


        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            throw new CustomExceptionNotFound("Cart not found ");


        // Method to remove product from cart
       return removeProductFromCartFunction(cart, removeCartItemFromCartDto.getCartItemId());


    }

    /**
     * Method to remove Recipe from cart
     */
    public Object  removeProductFromCartFunction(Cart cart, Long cartItemId ){
        int countCartItems=cartItemsRepository.countAllByCartIdAndStatus(cart.getId(),RecordStatus.ACTIVE.toString());
        CartItems cartItem= cartItemsRepository.findFirstByIdAndCartIdAndStatus(cartItemId,cart.getId(),RecordStatus.ACTIVE.toString());
        if (null==cartItem)
            throw new CustomExceptionNotFound("Item not available");

        cartItem.setStatus(RecordStatus.DELETED.toString());
        cartItemsRepository.save(cartItem);

        // check if all items removed in cart
        if(countCartItems<=1){
            cart.setStatus(RecordStatus.DELETED.toString());
            cartRepository.save(cart);
        }
        return  new ResponseModel("success","Recipe removed successfully");
    }

    public  ResponseModel cartDetails() {

        Users user = appFunctions.getLoginUser();
        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            throw new CustomExceptionNotFound("Cart not found");
        return new ResponseModel("success","success", getPreparedCarDetails(cart));
    }

    public  CartDetailsResponseDto getPreparedCarDetails(Cart cart){
        List<CartItems> cartItems= cartItemsRepository.findAllByCartIdAndStatus(cart.getId(),RecordStatus.ACTIVE.toString());
        CartDetailsResponseDto cartDetailsResponseDto=new CartDetailsResponseDto();
        List<CartDetailsResponseDto.ItemDetails> itemDetails=new ArrayList<>();

        AtomicReference<BigDecimal> total= new AtomicReference<>(BigDecimal.ZERO);
        cartItems.forEach(cartItem -> {
            CartDetailsResponseDto.ItemDetails itemDetail=new CartDetailsResponseDto.ItemDetails();
            itemDetail.setId(cartItem.getId());
            itemDetail.setRecipeName(cartItem.getRecipeLink() !=null ? cartItem.getRecipeLink().getName() :"");
            itemDetail.setPricePerPilotes(cartItem.getPricePerPilote());
            itemDetail.setNoOfPilotes(cartItem.getPilotesCount());
            itemDetail.setRecipeId(cartItem.getRecipeId());
            itemDetails.add(itemDetail);
            total.set( total.get().add(cartItem.getPricePerPilote().multiply(BigDecimal.valueOf(cartItem.getPilotesCount()))));
        });
        cartDetailsResponseDto.setCartId(cart.getId());
        cartDetailsResponseDto.setItemDetails(itemDetails);
        cartDetailsResponseDto.setTotalAmount(total.get());

        return  cartDetailsResponseDto;

    }


    public Object modifyCartItemDetails(ModifyCartItemDto modifyCartItemDto){

        Users user = appFunctions.getLoginUser();


        Cart cart = cartRepository.findFirstByUserIdAndStatus(user.getId(), RecordStatus.ACTIVE.toString());
        if (null == cart)
            throw new CustomExceptionNotFound("Cart not found ");


        CartItems cartItem= cartItemsRepository.findFirstByIdAndCartIdAndStatus(modifyCartItemDto.getCartItemId(),cart.getId(),RecordStatus.ACTIVE.toString());
        if (null==cartItem)
            throw new CustomExceptionNotFound("Item not available ");

        //check if no of pilotes are supported
        if (applicationProperties.getSupportedPilotsCount().stream().noneMatch(conf->conf==modifyCartItemDto.getNumberOfPilotes().intValue())){
            throw new CustomExceptionNotFound("No of pilotes is not in our list");

        }

        cartItem.setTotalAmount(cartItem.getRecipeLink().getPrice() !=null ? cartItem.getRecipeLink().getPrice(): BigDecimal.ZERO.multiply(new BigDecimal(modifyCartItemDto.getNumberOfPilotes())));
        cartItem.setPilotesCount(modifyCartItemDto.getNumberOfPilotes());
        cartItem.setUpdatedAt(new Date());
        cartItem=cartItemsRepository.save(cartItem);

        return new ResponseModel("sucesss", "Modified cart details",cartItem);


    }



}
