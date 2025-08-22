package com.example.recipes.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.recipes.entity.Recipe;
import com.example.recipes.repo.RecipeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipeImporter implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RecipeImporter.class);
    
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        if (recipeRepository.count() > 0) {
            logger.info("Recipes already in database, skipping import.");
            return;
        }

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("US_recipes_null.json");
            if (is == null) {
                logger.warn("Could not find US_recipes_null.json in classpath");
                return;
            }

            logger.info("Starting recipe data import...");
            

            Map<String, Map<String, Object>> root =
                    objectMapper.readValue(is, new TypeReference<>() {});
            List<Map<String, Object>> rawList = new ArrayList<>(root.values());

            List<Recipe> recipes = new ArrayList<>();
            int processed = 0;

            for (Map<String, Object> raw : rawList) {
                try {
                    Recipe recipe = mapRawDataToRecipe(raw);
                    recipes.add(recipe);
                    processed++;
                    
                    if (processed % 1000 == 0) {
                        logger.info("Processed {} recipes...", processed);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to process recipe: {}", e.getMessage());
                }
            }


            recipeRepository.saveAll(recipes);
            logger.info("Successfully imported {} recipes", recipes.size());
            
        } catch (Exception e) {
            logger.error("Failed to import recipes: {}", e.getMessage(), e);
        }
    }

    private Recipe mapRawDataToRecipe(Map<String, Object> raw) {
        Recipe recipe = new Recipe();
        
    
        recipe.setTitle((String) raw.get("title"));
        recipe.setCuisine((String) raw.get("cuisine"));
        recipe.setDescription((String) raw.get("description"));
        recipe.setUrl((String) raw.get("URL"));

        Object ratingObj = raw.get("rating");
        if (ratingObj != null) {
            try {
                recipe.setRating(Double.valueOf(ratingObj.toString()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid rating value: {}", ratingObj);
            }
        }

    
        setTimeValue(raw, "prep_time", recipe::setPrep_time);
        setTimeValue(raw, "cook_time", recipe::setCook_time);
        setTimeValue(raw, "total_time", recipe::setTotal_time);

        Object ingredientsObj = raw.get("ingredients");
        if (ingredientsObj != null) {
            try {
                recipe.setIngredients(objectMapper.writeValueAsString(ingredientsObj));
            } catch (Exception e) {
                logger.warn("Failed to serialize ingredients: {}", e.getMessage());
            }
        }

    
        Object instructionsObj = raw.get("instructions");
        if (instructionsObj != null) {
            try {
                recipe.setInstructions(objectMapper.writeValueAsString(instructionsObj));
            } catch (Exception e) {
                logger.warn("Failed to serialize instructions: {}", e.getMessage());
            }
        }

   
        Object nutrientsObj = raw.get("nutrients");
        if (nutrientsObj instanceof Map<?, ?> map) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> nutrientsMap = (Map<String, Object>) map;
                recipe.setNutrients(nutrientsMap);
            } catch (Exception e) {
                logger.warn("Failed to set nutrients: {}", e.getMessage());
            }
        }

   
        Object servesObj = raw.get("serves");
        if (servesObj != null) {
            recipe.setServes(servesObj.toString());
        }

        return recipe;
    }

    private void setTimeValue(Map<String, Object> raw, String fieldName, java.util.function.Consumer<Integer> setter) {
        Object timeObj = raw.get(fieldName);
        if (timeObj != null) {
            try {
                setter.accept(Integer.valueOf(timeObj.toString()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid {} value: {}", fieldName, timeObj);
            }
        }
    }
}
