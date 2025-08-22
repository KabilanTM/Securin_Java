package com.example.recipes.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.recipes.dto.RecipeDto;
import com.example.recipes.service.RecipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@CrossOrigin 
public class RecipeController {

    private final RecipeService service;

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return service.listAll(page, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        return service.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

  
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = false) String calories,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String cuisine,
            @RequestParam(name = "total_time", required = false) String totalTime,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return service.search(calories, title, cuisine, totalTime, rating, page, limit);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStatistics() {
        return service.getStatistics();
    }
}
