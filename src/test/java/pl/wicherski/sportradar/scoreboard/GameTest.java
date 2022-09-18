package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static pl.wicherski.sportradar.scoreboard.GameAssert.assertThatGame;

class GameTest {

    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";

    @Test
    void shouldGenerateGameCopyWithNewScore_whenUpdatingScore() {
        Instant creationTimestamp = Instant.now();
        Game originalGame = new Game(TEAM_1, TEAM_2, Score.of(0, 0), creationTimestamp);
        Score newScore = Score.of(1, 2);

        Game updatedGame = originalGame.withUpdatedScore(newScore);

        assertThatGame(updatedGame).hasHomeTeam(TEAM_1)
                                   .hasAwayTeam(TEAM_2)
                                   .hasScore(newScore)
                                   .wasCreatedAt(creationTimestamp);
    }

    @Test
    void shouldNotChangeOriginalGameScore_whenUpdatingScore() {
        Score originalScore = Score.of(0, 0);
        Game originalGame = new Game(TEAM_1, TEAM_2, originalScore, Instant.now());

        originalGame.withUpdatedScore(Score.of(1, 2));

        assertThatGame(originalGame).hasHomeTeam(TEAM_1)
                                    .hasAwayTeam(TEAM_2)
                                    .hasScore(originalScore);
    }

}