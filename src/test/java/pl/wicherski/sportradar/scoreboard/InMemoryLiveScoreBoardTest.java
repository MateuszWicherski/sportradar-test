package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLiveScoreBoardTest {

    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";

    private InMemoryLiveScoreBoard board;
    private Map<GameId, Game> storedGames;

    @BeforeEach
    void setUp() {
        storedGames = new HashMap<>();
        board = new InMemoryLiveScoreBoard(storedGames);
    }

    @Test
    void shouldReturnStartedGameId_whenStartingGame() {
        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        assertThat(gameId).isNotNull();
    }

    @Test
    void shouldReturnDifferentGameIdEachTime_whenStartingGame() {
        GameId gameId1 = board.startGame(TEAM_1, TEAM_2);
        GameId gameId2 = board.startGame("team3", "team4");

        assertThat(gameId1).isNotEqualTo(gameId2);
    }

    @Test
    void shouldStoreStartedGame_whenStartingGame() {
        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        Game expectedGame = new Game(TEAM_1, TEAM_2);
        assertThat(storedGames).hasSize(1)
                               .extractingByKey(gameId)
                               .isEqualTo(expectedGame);
    }

    @Test
    void shouldStoreAllStartedGames_whenStartingGame_multipleTimes() {
        board.startGame(TEAM_1, TEAM_2);
        board.startGame("team3", "team4");

        assertThat(storedGames).hasSize(2);
    }

}