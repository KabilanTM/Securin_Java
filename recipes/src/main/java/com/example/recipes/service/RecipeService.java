package com.example.recipes.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.entity.Recipe;
import com.example.recipes.repo.RecipeRepository;
import com.example.recipes.repo.RecipeSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    
    private final RecipeRepository recipeRepository;
    private final RecipeSearchRepository recipeSearchRepository;
    private final ObjectMapper objectMapper;

    public List<Recipe> getAllRecipes() {
        logger.debug("Fetching all recipes");
        return recipeRepository.findAll();
    }

    public Optional<RecipeDto> getRecipeById(Long id) {
        logger.debug("Fetching recipe by ID: {}", id);
        try {
            return recipeRepository.findById(id)
                    .map(RecipeDto::from);
        } catch (Exception e) {
            logger.error("Error fetching recipe with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch recipe", e);
        }
    }

    public Map<String, Object> listAll(int page, int limit) {
        logger.debug("Fetching recipes page {} with limit {}", page, limit);
        try {
            RecipeSearchRepository.SearchParams params = new RecipeSearchRepository.SearchParams();
            params.page = page;
            params.limit = limit;
            
            RecipeSearchRepository.PagedResult result = recipeSearchRepository.search(params);
            
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("limit", limit);
            response.put("total", result.total);
            response.put("data", result.data);
            
            logger.info("Fetched {} recipes (page {}, limit {})", result.data.size(), page, limit);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching recipes list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch recipes list", e);
        }
    }

    public Map<String, Object> search(String calories, String title, String cuisine, String totalTime, String rating, Integer page, Integer limit) {
        logger.debug("Searching recipes with filters - calories: {}, title: {}, cuisine: {}, totalTime: {}, rating: {}, page: {}, limit: {}",
                calories, title, cuisine, totalTime, rating, page, limit);
        
        try {
            RecipeSearchRepository.SearchParams params = new RecipeSearchRepository.SearchParams();
            params.caloriesExpr = calories;
            params.title = title;
            params.cuisine = cuisine;
            params.totalTimeExpr = totalTime;
            params.ratingExpr = rating;
            params.page = page != null ? page : 1;
            params.limit = limit != null ? limit : 10;
            
            RecipeSearchRepository.PagedResult result = recipeSearchRepository.search(params);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", result.data);
            
            logger.info("Search found {} recipes with given filters", result.data.size());
            return response;
        } catch (Exception e) {
            logger.error("Error searching recipes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search recipes", e);
        }
    }

    @Cacheable("recipeStatistics")
    public Map<String, Object> getStatistics() {
        logger.debug("Fetching recipe statistics");
        try {
            Map<String, Object> stats = new HashMap<>();
            
           
            long totalRecipes = recipeRepository.count();
            stats.put("totalRecipes", totalRecipes);
      
            List<Object[]> cuisineCounts = recipeRepository.countByCuisine();
            Map<String, Long> cuisineStats = new HashMap<>();
            for (Object[] result : cuisineCounts) {
                cuisineStats.put((String) result[0], (Long) result[1]);
            }
            stats.put("recipesByCuisine", cuisineStats);
      
            Double averageRating = recipeRepository.getAverageRating();
            stats.put("averageRating", averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0);
            
        
            Double averageTotalTime = recipeRepository.getAverageTotalTime();
            stats.put("averageTotalTime", averageTotalTime != null ? Math.round(averageTotalTime) : 0);
            
            logger.info("Fetched statistics: {} total recipes, {} cuisines", totalRecipes, cuisineStats.size());
            return stats;
        } catch (Exception e) {
            logger.error("Error fetching statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch statistics", e);
        }
    }
}
