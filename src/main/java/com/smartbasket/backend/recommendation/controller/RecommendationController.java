package com.smartbasket.backend.recommendation.controller;

import com.smartbasket.backend.recommendation.dto.RecommendationRequest;
import com.smartbasket.backend.recommendation.dto.RecommendationResponse;
import com.smartbasket.backend.recommendation.service.RecommendationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<RecommendationResponse> generateRecommendation(
            @Valid @RequestBody RecommendationRequest request
    ) {

        RecommendationResponse response =
                recommendationService.generateRecommendation(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}