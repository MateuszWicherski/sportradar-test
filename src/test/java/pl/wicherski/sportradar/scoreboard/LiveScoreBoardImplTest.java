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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.wicherski.sportradar.scoreboard.GameAssert.assertThatGame;

@ExtendWith(MockitoExtension.class)
class LiveScoreBoardImplTest {

    private static final String TEAM_1 = "team1";
    private static final String TEAM_2 = "team2";

    private LiveScoreBoardImpl board;
    @Mock
    private GameRepository gameRepositoryMock;
    @Mock
    private TimeProvider timeProviderMock;
    @Mock
    private ScoreSummaryFactory scoreSummaryFactoryMock;
    @Captor
    private ArgumentCaptor<List<Game>> gamesListCaptor;
    @Captor
    private ArgumentCaptor<Game> gameCaptor;

    @BeforeEach
    void setUp() {
        board = new LiveScoreBoardImpl(gameRepositoryMock, timeProviderMock, scoreSummaryFactoryMock,
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

        verify(gameRepositoryMock).save(eq(gameId), gameCaptor.capture());
        assertThatGame(gameCaptor.getValue()).hasHomeTeam(TEAM_1)
                                             .hasAwayTeam(TEAM_2);
    }

    @Test
    void shouldStoreAllStartedGames_whenStartingGame_multipleTimes() {
        board.startGame(TEAM_1, TEAM_2);
        board.startGame("team3", "team4");

        verify(gameRepositoryMock, times(2)).save(any(), any());
    }

    @Test
    void shouldStartGameWithScoreZeroToZero_whenStartingGame() {
        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        verify(gameRepositoryMock).save(eq(gameId), gameCaptor.capture());
        assertThatGame(gameCaptor.getValue()).hasScore(Score.of(0, 0));
    }

    @Test
    void shouldSetCreationTimestampToNow_whenStartingGame() {
        Instant now = Instant.now();
        when(timeProviderMock.now()).thenReturn(now);

        GameId gameId = board.startGame(TEAM_1, TEAM_2);

        verify(gameRepositoryMock).save(eq(gameId), gameCaptor.capture());
        assertThatGame(gameCaptor.getValue()).wasCreatedAt(now);
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
        GameId gameId = GameId.generate();

        board.finishGame(gameId);

        verify(gameRepositoryMock).delete(gameId);
    }

    @Test
    void shouldDoNothing_whenFinishingTheGame_thatDoesNotExist() {
        GameId gameId = GameId.generate();

        assertThatCode(() -> board.finishGame(gameId)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFinishingTheGame_withNullId() {
        assertThatThrownBy(() -> board.finishGame(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUpdateGameScore_whenUpdatingScore() {
        GameId gameId = GameId.generate();
        Game game = new Game(TEAM_1, TEAM_2, Score.of(0, 0), Instant.now());
        when(gameRepositoryMock.get(gameId)).thenReturn(Optional.of(game));
        Score newScore = Score.of(1, 2);

        board.updateScore(gameId, newScore);

        verify(gameRepositoryMock).update(eq(gameId), gameCaptor.capture());
        assertThatGame(gameCaptor.getValue()).hasScore(newScore);
    }

    @Test
    void shouldThrowGameNotFoundException_whenUpdatingScore_ofNotTrackedGame() {
        GameId gameId = GameId.generate();
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
        GameId gameId = GameId.generate();

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
        when(gameRepositoryMock.getAll()).thenReturn(List.of(game1, game2, game3, game4));

        board.getSummary();

        verify(scoreSummaryFactoryMock).createSummaryFor(gamesListCaptor.capture());
        assertThat(gamesListCaptor.getValue()).containsExactly(game4, game2, game1, game3);
    }

    @Test
    void shouldReturnScoreSummary_whenCreatingSummary() {
        ScoreSummary scoreSummary = mock(ScoreSummary.class);
        when(scoreSummaryFactoryMock.createSummaryFor(any(List.class))).thenReturn(scoreSummary);

        ScoreSummary summary = board.getSummary();

        assertThat(summary).isSameAs(scoreSummary);
    }

}