package com.example.recipes.entity;

import java.util.Map;

import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255, message = "Cuisine must be less than 255 characters")
    private String cuisine;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be less than 500 characters")
    @Column(length = 500, nullable = false)
    private String title;

    @PositiveOrZero(message = "Rating must be a positive number or zero")
    @Column(precision = 3, scale = 2)
    private Double rating;

    @PositiveOrZero(message = "Prep time must be a positive number or zero")
    @Column(name = "prep_time")
    private Integer prep_time;

    @PositiveOrZero(message = "Cook time must be a positive number or zero")
    @Column(name = "cook_time")
    private Integer cook_time;

    @PositiveOrZero(message = "Total time must be a positive number or zero")
    @Column(name = "total_time")
    private Integer total_time;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 1000, message = "URL must be less than 1000 characters")
    @Column(length = 1000)
    private String url;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String ingredients;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> nutrients;

    @Size(max = 255, message = "Serves must be less than 255 characters")
    private String serves;

    @Column(name = "calories_num", insertable = false, updatable = false)
    private Integer caloriesNum;
}
