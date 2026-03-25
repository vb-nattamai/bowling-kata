package bowling.api;

import bowling.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@Import(LeaderboardService.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeaderboardService leaderboardService;

    @BeforeEach
    void clearLeaderboard() {
        leaderboardService.clear();
    }

    // -----------------------------------------------------------------------
    // POST /api/score
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Perfect game (12 strikes) should return 300")
    void perfectGame() throws Exception {
        List<Integer> rolls = List.of(
            10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10   // 12 strikes
        );
        ScoringRequest req = new ScoringRequest("Alice", rolls);

        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.playerName", is("Alice")))
            .andExpect(jsonPath("$.score",      is(300)))
            .andExpect(jsonPath("$.rank",        is(1)));
    }

    @Test
    @DisplayName("Gutter game (all zeros) should return 0")
    void gutterGame() throws Exception {
        List<Integer> rolls = java.util.Collections.nCopies(20, 0);
        ScoringRequest req = new ScoringRequest("Bob", rolls);

        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score", is(0)));
    }

    @Test
    @DisplayName("All ones should return 20")
    void allOnes() throws Exception {
        List<Integer> rolls = java.util.Collections.nCopies(20, 1);
        ScoringRequest req = new ScoringRequest("Carol", rolls);

        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score", is(20)));
    }

    @Test
    @DisplayName("Spare followed by 3 should score 16 for first two frames")
    void spareBonus() throws Exception {
        List<Integer> rolls = new java.util.ArrayList<>();
        rolls.add(5); rolls.add(5); // spare
        rolls.add(3); rolls.add(0); // frame 2
        for (int i = 0; i < 16; i++) rolls.add(0);  // remaining 8 frames

        ScoringRequest req = new ScoringRequest("Dave", rolls);

        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score", is(16)));
    }

    // -----------------------------------------------------------------------
    // GET /api/leaderboard
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Leaderboard returns highest score first")
    void leaderboardOrder() throws Exception {
        // Record two scores
        postScore("Low",  java.util.Collections.nCopies(20, 1));  // 20
        postScore("High", List.of(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10)); // 300

        mockMvc.perform(get("/api/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].playerName", is("High")))
            .andExpect(jsonPath("$[0].score",      is(300)))
            .andExpect(jsonPath("$[1].playerName", is("Low")))
            .andExpect(jsonPath("$[1].score",      is(20)));
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private void postScore(String player, List<Integer> rolls) throws Exception {
        ScoringRequest req = new ScoringRequest(player, rolls);
        mockMvc.perform(post("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }
}
