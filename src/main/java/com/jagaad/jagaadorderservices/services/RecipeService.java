package com.jagaad.jagaadorderservices.services;

import com.jagaad.jagaadorderservices.entities.Recipes;
import com.jagaad.jagaadorderservices.repositories.RecipesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RecipeService {

    private final RecipesRepository recipesRepository;

    public RecipeService(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    public Recipes  addRecipe(Recipes recipes){

       return recipesRepository.save(recipes);
    }
}
