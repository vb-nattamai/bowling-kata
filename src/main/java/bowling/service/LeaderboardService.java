package bowling.service;

import bowling.domain.PlayerScore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory leaderboard service.
 * Stores all recorded {@link PlayerScore} objects and returns them sorted
 * highest-first on demand.
 *
 * <p>Thread-safe: uses {@link CopyOnWriteArrayList} for concurrent access.</p>
 */
@Service
public class LeaderboardService {

    private final CopyOnWriteArrayList<PlayerScore> scores = new CopyOnWriteArrayList<>();

    /**
     * Record a new score and return the player's rank (1-based, highest score = rank 1).
     *
     * @param playerScore the score to add
     * @return rank after insertion
     */
    public int record(PlayerScore playerScore) {
        scores.add(playerScore);
        List<PlayerScore> sorted = sorted();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getPlayerName().equals(playerScore.getPlayerName())
                    && sorted.get(i).getScore() == playerScore.getScore()) {
                return i + 1;
            }
        }
        return sorted.size();
    }

    /**
     * Return the top {@code limit} scores, sorted highest-first.
     */
    public List<PlayerScore> getTopScores(int limit) {
        List<PlayerScore> sorted = sorted();
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    /** Returns a freshly sorted snapshot of all recorded scores. */
    private List<PlayerScore> sorted() {
        List<PlayerScore> copy = new ArrayList<>(scores);
        Collections.sort(copy);
        return copy;
    }

    /** Clears all scores (useful for test isolation). */
    public void clear() {
        scores.clear();
    }
}
