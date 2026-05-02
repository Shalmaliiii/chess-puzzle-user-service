# Chess Puzzle User Service

A Spring Boot microservice handling user authentication, profiles, ratings, and leaderboards for the Chess Puzzle Platform.

## Tech Stack

- **Java 21** (LTS)
- **Spring Boot 3.4.x**
- **Spring Security** with JWT authentication
- **Spring Data MongoDB**
- **Spring Kafka** for event-driven updates
- **Gradle** (Groovy DSL)

## Features

- **User Registration & Login** with JWT-based authentication
- **User Profiles** with puzzle-solving statistics
- **ELO Rating System** that updates based on puzzle performance
- **Leaderboard** sorted by rating
- **Kafka Consumer** for real-time stat updates on puzzle completion

## API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and get tokens | No |
| POST | `/api/auth/refresh` | Refresh access token | No |

### Users (`/api/users`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/me` | Get current user profile | JWT |
| GET | `/api/users/leaderboard?limit=50` | Get top players | No |
| GET | `/api/users/{id}/stats` | Get user statistics | JWT |

## Running Locally

### Prerequisites

- Java 21
- MongoDB running on `localhost:27017`
- Kafka running on `kafka:9092` (optional, for puzzle events)

### Build & Run

```bash
./gradlew build
./gradlew bootRun
```

The service starts on **port 8081**.

### Run Tests

```bash
./gradlew test
```

### Docker

```bash
docker build -t chess-puzzle-user-service .
docker run -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=your-secret-key \
  chess-puzzle-user-service
```

## Configuration

| Property | Default | Environment Variable |
|----------|---------|---------------------|
| Server Port | 8081 | `SERVER_PORT` |
| MongoDB URI | `mongodb://localhost:27017/chess_users` | `SPRING_DATA_MONGODB_URI` |
| Kafka Servers | `kafka:9092` | `SPRING_KAFKA_BOOTSTRAP_SERVERS` |
| JWT Secret | (built-in default) | `JWT_SECRET` |
| JWT Expiration | 3600000 (1 hour) | `JWT_EXPIRATION` |

## Kafka Events

### Consumed: `puzzle.solved`

```json
{
  "userId": "string",
  "puzzleId": "string",
  "difficulty": "BEGINNER|INTERMEDIATE|ADVANCED|MASTER",
  "timeMs": 12345,
  "correct": true,
  "mateIn": 2
}
```

On receiving this event, the service updates the user's stats, recalculates their ELO rating, and adds the puzzle to their recent history.

## Rating System

Uses a simplified ELO calculation:

- **K-Factor**: 32 (rating < 1600), 24 (< 2000), 16 (≥ 2000)
- **Difficulty Ratings**: Beginner=800, Intermediate=1200, Advanced=1600, Master=2000
- Rating change = K × (actual − expected)
