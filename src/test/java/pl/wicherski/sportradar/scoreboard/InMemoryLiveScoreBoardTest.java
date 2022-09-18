package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLiveScoreBoardTest {

    private InMemoryLiveScoreBoard board;

    @BeforeEach
    void setUp() {
        board = new InMemoryLiveScoreBoard();
    }

    @Test
    void shouldReturnStartedGameId_whenStartingGame() {
        GameId gameId = board.startGame("team1", "team2");

        assertThat(gameId).isNotNull();
    }

    @Test
    void shouldReturnDifferentGameIdEachTime_whenStartingGame() {
        GameId gameId1 = board.startGame("team1", "team2");
        GameId gameId2 = board.startGame("team3", "team4");

        assertThat(gameId1).isNotEqualTo(gameId2);
    }

}