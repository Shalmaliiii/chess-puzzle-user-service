package com.puzzlegenerator.chess.user_service.controller;

import com.puzzlegenerator.chess.user_service.dto.response.LeaderboardEntry;
import com.puzzlegenerator.chess.user_service.dto.response.UserProfileResponse;
import com.puzzlegenerator.chess.user_service.model.UserStats;
import com.puzzlegenerator.chess.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication,
                                                               HttpServletRequest request) {
        String userId = resolveUserId(authentication, request);
        log.info("Profile request for user: {}", userId);
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        log.info("Leaderboard request with limit: {}", limit);
        List<LeaderboardEntry> leaderboard = userService.getLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStats> getUserStats(@PathVariable String id) {
        log.info("Stats request for user: {}", id);
        UserStats stats = userService.getUserStats(id);
        return ResponseEntity.ok(stats);
    }

    private String resolveUserId(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.getPrincipal() != null) {
            return authentication.getPrincipal().toString();
        }
        String headerUserId = request.getHeader("X-User-Id");
        if (StringUtils.hasText(headerUserId)) {
            return headerUserId;
        }
        throw new IllegalStateException("Unable to determine user identity");
    }
}
