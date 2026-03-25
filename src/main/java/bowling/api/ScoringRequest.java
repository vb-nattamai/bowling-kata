package bowling.api;

import java.util.List;

/**
 * Request body for POST /api/score.
 * Accepts a player name and a list of roll values (0-10 per roll).
 */
public class ScoringRequest {

    private String playerName;
    private List<Integer> rolls;

    public ScoringRequest() {}

    public ScoringRequest(String playerName, List<Integer> rolls) {
        this.playerName = playerName;
        this.rolls = rolls;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public List<Integer> getRolls() { return rolls; }
    public void setRolls(List<Integer> rolls) { this.rolls = rolls; }
}
