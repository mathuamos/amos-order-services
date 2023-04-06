package com.jagaad.jagaadorderservices.services;

import static org.mockito.Mockito.*;

import com.jagaad.jagaadorderservices.entities.Recipes;
import com.jagaad.jagaadorderservices.repositories.RecipesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

public class RecipeServiceTest {

    @Mock
    private RecipesRepository recipesRepository;

    private RecipeService recipeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeService(recipesRepository);
    }

    @Test
    public void testAddRecipe() {
        Recipes recipe = new Recipes();
        recipe.setName("Test Recipe");
        recipe.setDescription("Test description");
        recipe.setPrice(BigDecimal.valueOf(10.99));
        recipe.setPriority(1);

        when(recipesRepository.save(recipe)).thenReturn(recipe);

        Recipes savedRecipe = recipeService.addRecipe(recipe);

        verify(recipesRepository, times(1)).save(recipe);

        Assertions.assertEquals(recipe, savedRecipe, "Saved recipe should be equal to the original recipe");
    }
}
