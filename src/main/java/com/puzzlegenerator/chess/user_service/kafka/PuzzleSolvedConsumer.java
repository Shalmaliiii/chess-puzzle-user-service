package com.puzzlegenerator.chess.user_service.kafka;

import com.puzzlegenerator.chess.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PuzzleSolvedConsumer {

    private final UserService userService;

    public PuzzleSolvedConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "puzzle.solved", groupId = "user-service")
    public void onPuzzleSolved(PuzzleSolvedEvent event) {
        log.info("Received puzzle.solved event: userId={}, puzzleId={}, correct={}",
                event.getUserId(), event.getPuzzleId(), event.isCorrect());

        try {
            userService.updateUserAfterPuzzleSolve(
                    event.getUserId(),
                    event.getPuzzleId(),
                    event.getDifficulty(),
                    event.getTimeMs(),
                    event.isCorrect()
            );
        } catch (Exception e) {
            log.error("Failed to process puzzle.solved event for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}
