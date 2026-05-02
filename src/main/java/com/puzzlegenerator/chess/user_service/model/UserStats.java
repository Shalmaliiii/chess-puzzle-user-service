package com.puzzlegenerator.chess.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {

    private int totalSolved;

    private int totalAttempted;

    private double accuracy;

    private int currentStreak;

    private int bestStreak;

    private long averageSolveTimeMs;

    @Builder.Default
    private Map<String, DifficultyStats> byDifficulty = new HashMap<>();

    public Map<String, DifficultyStats> getByDifficulty() {
        if (byDifficulty == null) {
            byDifficulty = new HashMap<>();
        }
        return byDifficulty;
    }
}
