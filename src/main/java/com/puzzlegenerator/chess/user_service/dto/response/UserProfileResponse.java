package com.puzzlegenerator.chess.user_service.dto.response;

import com.puzzlegenerator.chess.user_service.model.RecentPuzzle;
import com.puzzlegenerator.chess.user_service.model.UserRole;
import com.puzzlegenerator.chess.user_service.model.UserStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String id;
    private String username;
    private String email;
    private UserRole role;
    private int rating;
    private UserStats stats;
    private List<RecentPuzzle> recentPuzzles;
}
