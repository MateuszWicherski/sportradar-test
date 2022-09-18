package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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

    @Test
    void shouldThrowIllegalArgumentException_whenStartingGame_andHomeTeamNameIsNull() {
        assertThatThrownBy(() -> board.startGame(null, TEAM_2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartingGame_andAwayTeamNameIsNull() {
        assertThatThrownBy(() -> board.startGame(TEAM_1, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRemoveGameFromStore_whenFinishingTheGame() {
        GameId gameId = new GameId();
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2));

        board.finishGame(gameId);

        assertThat(storedGames).isEmpty();
    }

    @Test
    void shouldRemoveOnlyFinishedGameFromStore_whenFinishingTheGame() {
        GameId gameId1 = new GameId();
        GameId gameId2 = new GameId();
        storedGames.put(gameId1, new Game(TEAM_1, TEAM_2));
        Game retainedGame = new Game("team3", "team4");
        storedGames.put(gameId2, retainedGame);

        board.finishGame(gameId1);

        assertThat(storedGames).hasSize(1)
                               .extractingByKey(gameId2)
                               .isEqualTo(retainedGame);
    }

    @Test
    void shouldDoNothing_whenFinishingTheGame_thatDoesNotExist() {
        GameId gameId = new GameId();

        assertThatCode(() -> board.finishGame(gameId)).doesNotThrowAnyException();
    }

    @Test
    void shouldDoNothing_whenFinishingTheGame_thatHasAlreadyBeenFinished() {
        GameId gameId = new GameId();
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2));
        board.finishGame(gameId);

        assertThatCode(() -> board.finishGame(gameId)).doesNotThrowAnyException();
    }

}