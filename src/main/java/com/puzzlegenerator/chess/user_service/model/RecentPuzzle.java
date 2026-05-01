package com.puzzlegenerator.chess.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPuzzle {

    private String puzzleId;
    private Instant solvedAt;
    private long timeMs;
    private boolean correct;
    private String difficulty;
}
