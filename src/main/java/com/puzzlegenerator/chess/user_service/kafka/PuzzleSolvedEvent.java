package com.puzzlegenerator.chess.user_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleSolvedEvent {

    private String userId;
    private String puzzleId;
    private String difficulty;
    private long timeMs;
    private boolean correct;
    private Integer mateIn;
}
