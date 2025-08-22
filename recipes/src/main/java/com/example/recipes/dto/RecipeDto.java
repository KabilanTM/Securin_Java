package com.example.recipes.dto;

import com.example.recipes.entity.Recipe;
import lombok.*;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeDto {
    private Long id;
    private String title;
    private String cuisine;
    private Double rating;
    private Integer prep_time;
    private Integer cook_time;
    private Integer total_time;
    private String description;
    private Map<String, Object> nutrients;
    private String serves;

    public static RecipeDto from(Recipe r) {
        return RecipeDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .cuisine(r.getCuisine())
                .rating(r.getRating())
                .prep_time(r.getPrep_time())
                .cook_time(r.getCook_time())
                .total_time(r.getTotal_time())
                .description(r.getDescription())
                .nutrients(r.getNutrients())
                .serves(r.getServes())
                .build();
    }
}
