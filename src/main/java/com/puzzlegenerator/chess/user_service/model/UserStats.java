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

    @Builder.Default
    private int totalSolved = 0;

    @Builder.Default
    private int totalAttempted = 0;

    @Builder.Default
    private double accuracy = 0.0;

    @Builder.Default
    private int currentStreak = 0;

    @Builder.Default
    private int bestStreak = 0;

    @Builder.Default
    private long averageSolveTimeMs = 0;

    @Builder.Default
    private Map<String, DifficultyStats> byDifficulty = new HashMap<>();
}
