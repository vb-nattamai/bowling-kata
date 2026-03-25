package bowling.api;

import bowling.service.LeaderboardService;
import bowling.domain.PlayerScore;
import bowling.Bowling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing bowling scoring endpoints.
 *
 * POST /api/score  — calculates a player's score and records it on the leaderboard.
 * GET  /api/leaderboard — returns the current top-10 leaderboard.
 */
@RestController
@RequestMapping("/api")
public class GameController {

    private final LeaderboardService leaderboardService;

    public GameController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/score")
    public ResponseEntity<ScoringResponse> score(@RequestBody ScoringRequest request) {
        int[] rolls = request.getRolls().stream().mapToInt(Integer::intValue).toArray();
        Bowling game = new Bowling();
        for (int pin : rolls) {
            game.roll(pin);
        }
        int totalScore = game.score();

        PlayerScore playerScore = new PlayerScore(request.getPlayerName(), totalScore);
        int rank = leaderboardService.record(playerScore);

        return ResponseEntity.ok(new ScoringResponse(request.getPlayerName(), totalScore, rank));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerScore>> leaderboard() {
        return ResponseEntity.ok(leaderboardService.getTopScores(10));
    }
}
