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

        assertThat(storedGames).hasSize(1)
                               .extractingByKey(gameId)
                               .extracting(Game::homeTeamName, Game::awayTeamName)
                               .containsExactly(TEAM_1, TEAM_2);
    }

    @Test
    void shouldStoreAllStartedGames_whenStartingGame_multipleTimes() {
        board.startGame(TEAM_1, TEAM_2);
        board.startGame("team3", "team4");

        assertThat(storedGames).hasSize(2);
    }

    @Test
    void shouldStartGameWithScoreZeroToZero_whenStartingGame() {
        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        Score expectedScore = Score.of(0, 0);
        assertThat(storedGames).extractingByKey(gameId)
                               .extracting(Game::score)
                               .isEqualTo(expectedScore);

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
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2, Score.of(0, 0)));

        board.finishGame(gameId);

        assertThat(storedGames).isEmpty();
    }

    @Test
    void shouldRemoveOnlyFinishedGameFromStore_whenFinishingTheGame() {
        GameId gameId1 = new GameId();
        GameId gameId2 = new GameId();
        storedGames.put(gameId1, new Game(TEAM_1, TEAM_2, Score.of(0, 0)));
        Game retainedGame = new Game("team3", "team4", Score.of(0, 0));
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
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2, Score.of(0, 0)));
        board.finishGame(gameId);

        assertThatCode(() -> board.finishGame(gameId)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFinishingTheGame_withNullId() {
        assertThatThrownBy(() -> board.finishGame(null)).isInstanceOf(IllegalArgumentException.class);
    }

}