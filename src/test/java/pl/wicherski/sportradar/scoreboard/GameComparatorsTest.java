package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.wicherski.sportradar.scoreboard.GameComparators.HIGHEST_OVERALL_SCORE;
import static pl.wicherski.sportradar.scoreboard.GameComparators.RECENT_FIRST;

class GameComparatorsTest {

    @Test
    void shouldCompareGamesByOverallScore() {
        Game game1 = new Game("team1", "team2", Score.of(1, 2), Instant.now());
        Game game2 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        Game game3 = new Game("team1", "team2", Score.of(1, 0), Instant.now());

        assertThat(List.of(game1, game2, game3)).isSortedAccordingTo(HIGHEST_OVERALL_SCORE);
    }

    @Test
    void shouldCompareGamesByCreationDate() {
        Instant now = Instant.now();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), now);
        Game game2 = new Game("team1", "team2", Score.of(1, 1), now.minusSeconds(1));
        Game game3 = new Game("team1", "team2", Score.of(1, 1), now.minusSeconds(2));

        assertThat(List.of(game1, game2, game3)).isSortedAccordingTo(RECENT_FIRST);
    }

}