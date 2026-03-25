package bowling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parameterized tests for the core {@link Bowling} scoring engine.
 * Demonstrates {@code @ParameterizedTest} with both {@code @CsvSource} and
 * {@code @MethodSource} — compatible with JUnit 5.8+.
 */
class BowlingParametrizedTest {

    /**
     * Roll the same pin count {@code times} times.
     */
    private Bowling gameWithRepeatedRolls(int pins, int times) {
        Bowling b = new Bowling();
        for (int i = 0; i < times; i++) b.roll(pins);
        return b;
    }

    // ------------------------------------------------------------------
    // Uniform-roll scenarios
    // ------------------------------------------------------------------

    @ParameterizedTest(name = "Rolling {0} pins × {1} times → expected score {2}")
    @CsvSource({
        " 0, 20,  0",   // gutter game
        " 1, 20, 20",   // all ones
        " 2, 20, 40",   // all twos
        " 4, 20, 80",   // all fours
        " 3, 20, 60",   // all threes
    })
    @DisplayName("Uniform rolls produce correct scores")
    void uniformRolls(int pins, int numRolls, int expected) {
        Bowling b = gameWithRepeatedRolls(pins, numRolls);
        assertEquals(expected, b.score());
    }

    // ------------------------------------------------------------------
    // Named scenarios via MethodSource
    // ------------------------------------------------------------------

    record GameScenario(String name, int[] rolls, int expected) {}

    static Stream<GameScenario> namedScenarios() {
        return Stream.of(
            new GameScenario("Perfect game",
                new int[]{10,10,10,10,10,10,10,10,10,10,10,10}, 300),
            new GameScenario("All spares (5+5) with 10 in last fill",
                repeat(5, 20, new int[]{10}), 155),
            new GameScenario("One strike then all zeros",
                prepend(10, zeros(18)), 10),
            new GameScenario("One spare (5+5) then 3 then zeros",
                concat(new int[]{5,5,3}, zeros(17)), 16)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("namedScenarios")
    @DisplayName("Named scoring scenarios")
    void namedScoringScenarios(GameScenario scenario) {
        Bowling b = new Bowling();
        for (int pin : scenario.rolls()) b.roll(pin);
        assertEquals(scenario.expected(), b.score(), scenario.name());
    }

    // ------------------------------------------------------------------
    // Utility helpers
    // ------------------------------------------------------------------

    private static int[] zeros(int count) {
        return new int[count];
    }

    private static int[] prepend(int first, int[] rest) {
        int[] result = new int[1 + rest.length];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }

    private static int[] concat(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static int[] repeat(int val, int count, int[] suffix) {
        int[] result = new int[count + suffix.length];
        for (int i = 0; i < count; i++) result[i] = val;
        System.arraycopy(suffix, 0, result, count, suffix.length);
        return result;
    }
}
