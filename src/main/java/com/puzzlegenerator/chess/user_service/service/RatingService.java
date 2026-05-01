package com.puzzlegenerator.chess.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RatingService {

    private static final Map<String, Integer> DIFFICULTY_RATINGS = Map.of(
            "BEGINNER", 800,
            "INTERMEDIATE", 1200,
            "ADVANCED", 1600,
            "MASTER", 2000
    );

    public int calculateNewRating(int currentRating, String difficulty, boolean solved) {
        int difficultyRating = DIFFICULTY_RATINGS.getOrDefault(difficulty.toUpperCase(), 1200);
        int kFactor = getKFactor(currentRating);

        double expectedScore = 1.0 / (1.0 + Math.pow(10, (difficultyRating - currentRating) / 400.0));
        double actualScore = solved ? 1.0 : 0.0;

        int newRating = (int) Math.round(currentRating + kFactor * (actualScore - expectedScore));
        log.debug("Rating update: {} -> {} (difficulty={}, solved={})", currentRating, newRating, difficulty, solved);
        return newRating;
    }

    private int getKFactor(int rating) {
        if (rating < 1600) return 32;
        if (rating < 2000) return 24;
        return 16;
    }
}
