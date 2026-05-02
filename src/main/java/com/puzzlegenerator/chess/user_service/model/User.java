package com.puzzlegenerator.chess.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Version
    private Long version;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String passwordHash;

    @Builder.Default
    private UserRole role = UserRole.USER;

    @Builder.Default
    private int rating = 1200;

    @Builder.Default
    private UserStats stats = new UserStats();

    @Builder.Default
    private List<RecentPuzzle> recentPuzzles = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
