package com.puzzlegenerator.chess.user_service.service;

import com.puzzlegenerator.chess.user_service.dto.request.LoginRequest;
import com.puzzlegenerator.chess.user_service.dto.request.RegisterRequest;
import com.puzzlegenerator.chess.user_service.dto.response.*;
import com.puzzlegenerator.chess.user_service.exception.DuplicateEmailException;
import com.puzzlegenerator.chess.user_service.exception.InvalidCredentialsException;
import com.puzzlegenerator.chess.user_service.exception.UserNotFoundException;
import com.puzzlegenerator.chess.user_service.model.DifficultyStats;
import com.puzzlegenerator.chess.user_service.model.RecentPuzzle;
import com.puzzlegenerator.chess.user_service.model.User;
import com.puzzlegenerator.chess.user_service.model.UserRole;
import com.puzzlegenerator.chess.user_service.model.UserStats;
import com.puzzlegenerator.chess.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RatingService ratingService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RatingService ratingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.ratingService = ratingService;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateEmailException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .rating(1200)
                .stats(new UserStats())
                .recentPuzzles(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        log.info("User registered: {}", user.getUsername());

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User logged in: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpiration())
                .build();
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        String newToken = jwtService.generateToken(user);

        return TokenResponse.builder()
                .token(newToken)
                .expiresIn(jwtService.getExpiration())
                .build();
    }

    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .rating(user.getRating())
                .stats(user.getStats())
                .recentPuzzles(user.getRecentPuzzles())
                .build();
    }

    public List<LeaderboardEntry> getLeaderboard(int limit) {
        List<User> topUsers = userRepository.findAllByOrderByRatingDesc(PageRequest.of(0, limit));

        return IntStream.range(0, topUsers.size())
                .mapToObj(i -> {
                    User u = topUsers.get(i);
                    return LeaderboardEntry.builder()
                            .rank(i + 1)
                            .username(u.getUsername())
                            .rating(u.getRating())
                            .totalSolved(u.getStats() != null ? u.getStats().getTotalSolved() : 0)
                            .build();
                })
                .toList();
    }

    public UserStats getUserStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return user.getStats();
    }

    public void updateUserAfterPuzzleSolve(String userId, String puzzleId, String difficulty,
                                            long timeMs, boolean correct) {
        String normalizedDifficulty = difficulty != null ? difficulty.toUpperCase() : "INTERMEDIATE";

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        UserStats stats = user.getStats();
        if (stats == null) {
            stats = new UserStats();
        }

        stats.setTotalAttempted(stats.getTotalAttempted() + 1);

        if (correct) {
            stats.setTotalSolved(stats.getTotalSolved() + 1);
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            if (stats.getCurrentStreak() > stats.getBestStreak()) {
                stats.setBestStreak(stats.getCurrentStreak());
            }

            long totalTime = stats.getAverageSolveTimeMs() * (stats.getTotalSolved() - 1) + timeMs;
            stats.setAverageSolveTimeMs(totalTime / stats.getTotalSolved());
        } else {
            stats.setCurrentStreak(0);
        }

        if (stats.getTotalAttempted() > 0) {
            stats.setAccuracy((double) stats.getTotalSolved() / stats.getTotalAttempted() * 100.0);
        }

        DifficultyStats diffStats = stats.getByDifficulty()
                .computeIfAbsent(normalizedDifficulty, k -> new DifficultyStats());
        diffStats.setAttempted(diffStats.getAttempted() + 1);
        if (correct) {
            diffStats.setSolved(diffStats.getSolved() + 1);
        }

        int newRating = ratingService.calculateNewRating(user.getRating(), normalizedDifficulty, correct);
        user.setRating(newRating);
        user.setStats(stats);

        RecentPuzzle recentPuzzle = RecentPuzzle.builder()
                .puzzleId(puzzleId)
                .solvedAt(Instant.now())
                .timeMs(timeMs)
                .correct(correct)
                .difficulty(difficulty)
                .build();

        List<RecentPuzzle> recentPuzzles = user.getRecentPuzzles();
        if (recentPuzzles == null) {
            recentPuzzles = new ArrayList<>();
        }
        recentPuzzles.addFirst(recentPuzzle);
        if (recentPuzzles.size() > 50) {
            recentPuzzles = new ArrayList<>(recentPuzzles.subList(0, 50));
        }
        user.setRecentPuzzles(recentPuzzles);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
        log.info("Updated user {} after puzzle solve: rating={}, correct={}", userId, newRating, correct);
    }
}
