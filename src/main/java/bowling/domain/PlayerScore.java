package bowling.domain;

import java.time.Instant;

/**
 * Domain object representing a player's scored game.
 */
public class PlayerScore implements Comparable<PlayerScore> {

    private final String playerName;
    private final int score;
    private final Instant recordedAt;

    public PlayerScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.recordedAt = Instant.now();
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public Instant getRecordedAt() { return recordedAt; }

    /** Natural order: highest score first. */
    @Override
    public int compareTo(PlayerScore other) {
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return "PlayerScore{playerName='" + playerName + "', score=" + score + '}';
    }
}
