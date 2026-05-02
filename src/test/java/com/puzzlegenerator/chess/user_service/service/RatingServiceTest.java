package com.puzzlegenerator.chess.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceTest {

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService();
    }

    @Test
    void shouldIncreaseRatingOnCorrectSolve() {
        int newRating = ratingService.calculateNewRating(1200, "INTERMEDIATE", true);
        assertTrue(newRating > 1200);
    }

    @Test
    void shouldDecreaseRatingOnIncorrectSolve() {
        int newRating = ratingService.calculateNewRating(1200, "INTERMEDIATE", false);
        assertTrue(newRating < 1200);
    }

    @Test
    void shouldGainMoreRatingForHarderPuzzle() {
        int gainMaster = ratingService.calculateNewRating(1200, "MASTER", true) - 1200;
        int gainBeginner = ratingService.calculateNewRating(1200, "BEGINNER", true) - 1200;
        assertTrue(gainMaster > gainBeginner);
    }

    @Test
    void shouldUseHigherKFactorForLowRatedPlayers() {
        int gainLow = ratingService.calculateNewRating(1000, "INTERMEDIATE", true) - 1000;
        int gainHigh = ratingService.calculateNewRating(2100, "INTERMEDIATE", true) - 2100;
        assertTrue(gainLow > gainHigh);
    }

    @Test
    void shouldHandleUnknownDifficultyGracefully() {
        int newRating = ratingService.calculateNewRating(1200, "UNKNOWN", true);
        assertTrue(newRating > 1200);
    }
}
