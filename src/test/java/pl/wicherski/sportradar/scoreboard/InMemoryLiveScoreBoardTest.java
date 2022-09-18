package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.wicherski.sportradar.scoreboard.GameAssert.assertThatGame;

@ExtendWith(MockitoExtension.class)
class InMemoryLiveScoreBoardTest {

    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";

    private InMemoryLiveScoreBoard board;
    private Map<GameId, Game> storedGames;
    @Mock
    private TimeProvider timeProviderMock;
    @Mock
    private ScoreSummaryFactory scoreSummaryFactoryMock;
    @Captor
    private ArgumentCaptor<List<Game>> gamesCaptor;

    @BeforeEach
    void setUp() {
        storedGames = new HashMap<>();
        board = new InMemoryLiveScoreBoard(storedGames, timeProviderMock, scoreSummaryFactoryMock,
                                           Comparator.comparing(Game::creationTimestamp));
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
                               .satisfies(game -> assertThatGame(game).hasHomeTeam(TEAM_1)
                                                                      .hasAwayTeam(TEAM_2));
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
                               .satisfies(game -> assertThatGame(game).hasScore(expectedScore));

    }

    @Test
    void shouldSetCreationTimestampToNow_whenStartingGame() {
        Instant now = Instant.now();
        when(timeProviderMock.now()).thenReturn(now);

        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        assertThat(storedGames).extractingByKey(gameId)
                               .satisfies(game -> assertThatGame(game).wasCreatedAt(now));
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
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now()));

        board.finishGame(gameId);

        assertThat(storedGames).isEmpty();
    }

    @Test
    void shouldRemoveOnlyFinishedGameFromStore_whenFinishingTheGame() {
        GameId gameId1 = new GameId();
        GameId gameId2 = new GameId();
        storedGames.put(gameId1, new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now()));
        Game retainedGame = new Game("team3", "team4", Score.of(0, 0), Instant.now());
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
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now()));
        board.finishGame(gameId);

        assertThatCode(() -> board.finishGame(gameId)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFinishingTheGame_withNullId() {
        assertThatThrownBy(() -> board.finishGame(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUpdateGameScore_whenUpdatingScore() {
        GameId gameId = new GameId();
        storedGames.put(gameId, new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now()));
        Score newScore = Score.of(1, 2);

        board.updateScore(gameId, newScore);

        assertThat(storedGames).extractingByKey(gameId)
                               .satisfies(game -> assertThatGame(game).hasScore(newScore));
    }

    @Test
    void shouldUpdateGameScoreOnlyOfGivenGame_whenUpdatingScore() {
        GameId gameId1 = new GameId();
        GameId gameId2 = new GameId();
        storedGames.put(gameId1, new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now()));
        Score game2OriginalScore = Score.of(0, 0);
        storedGames.put(gameId2, new Game("team3", "team4", game2OriginalScore, Instant.now()));
        Score newScore = Score.of(1, 2);

        board.updateScore(gameId1, newScore);

        assertThat(storedGames).extractingByKey(gameId2)
                               .satisfies(game -> assertThatGame(game).hasScore(game2OriginalScore));
    }

    @Test
    void shouldThrowGameNotFoundException_whenUpdatingScore_ofNotTrackedGame() {
        GameId gameId = new GameId();
        Score newScore = Score.of(1, 2);

        assertThatThrownBy(() -> board.updateScore(gameId, newScore)).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenUpdatingScore_withNullGameId() {
        Score newScore = Score.of(1, 2);

        assertThatThrownBy(() -> board.updateScore(null, newScore)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenUpdatingScore_withNullScore() {
        GameId gameId = new GameId();

        assertThatThrownBy(() -> board.updateScore(gameId, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSortGamesUsingComparator_whenCreatingSummary() {
        // ordering based on creation timestamp - see setUp
        Instant now = Instant.now();
        Game game1 = new Game("a", "b", Score.of(0, 0), now.minusSeconds(1));
        Game game2 = new Game("c", "d", Score.of(2, 2), now.minusSeconds(2));
        Game game3 = new Game("e", "f", Score.of(3, 0), now);
        Game game4 = new Game("g", "h", Score.of(0, 0), now.minusSeconds(4));
        storedGames.put(new GameId(), game1);
        storedGames.put(new GameId(), game2);
        storedGames.put(new GameId(), game3);
        storedGames.put(new GameId(), game4);

        board.getSummary();

        verify(scoreSummaryFactoryMock).createSummaryFor(gamesCaptor.capture());
        assertThat(gamesCaptor.getValue()).containsExactly(game4, game2, game1, game3);
    }

    @Test
    void shouldReturnScoreSummary_whenCreatingSummary() {
        ScoreSummary scoreSummary = mock(ScoreSummary.class);
        when(scoreSummaryFactoryMock.createSummaryFor(any(List.class))).thenReturn(scoreSummary);

        ScoreSummary summary = board.getSummary();

        assertThat(summary).isSameAs(scoreSummary);
    }

}