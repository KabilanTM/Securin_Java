package com.example.recipes.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.recipes.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT r.cuisine, COUNT(r) FROM Recipe r WHERE r.cuisine IS NOT NULL GROUP BY r.cuisine ORDER BY COUNT(r) DESC")
    List<Object[]> countByCuisine();
    
    @Query("SELECT AVG(r.rating) FROM Recipe r WHERE r.rating IS NOT NULL")
    Double getAverageRating();
    
    @Query("SELECT AVG(r.total_time) FROM Recipe r WHERE r.total_time IS NOT NULL")
    Double getAverageTotalTime();
}
