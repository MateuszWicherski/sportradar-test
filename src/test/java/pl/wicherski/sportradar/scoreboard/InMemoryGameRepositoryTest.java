package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class InMemoryGameRepositoryTest {

    private Map<GameId, Game> storedGames;
    private InMemoryGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        storedGames = new HashMap<>();
        gameRepository = new InMemoryGameRepository(storedGames);
    }

    @Test
    void shouldStoreGameInMemory_whenSavingGame() {
        GameId gameId = GameId.generate();
        Game game = new Game("team1", "team2", Score.of(1, 1), Instant.now());

        gameRepository.save(gameId, game);

        assertThat(storedGames).hasSize(1)
                               .containsEntry(gameId, game);
    }

    @Test
    void shouldOverwritePreviousGame_whenSavingGame_withTheSameId() {
        GameId gameId = GameId.generate();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        storedGames.put(gameId, game1);
        Game game2 = new Game("team2", "team3", Score.of(1, 1), Instant.now());

        gameRepository.save(gameId, game2);

        assertThat(storedGames).hasSize(1)
                               .containsEntry(gameId, game2);
    }

    @Test
    void shouldRemoveGameFromStore_whenDeletingGame() {
        GameId gameId = GameId.generate();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        storedGames.put(gameId, game1);

        gameRepository.delete(gameId);

        assertThat(storedGames).isEmpty();
    }

    @Test
    void shouldRemoveOnlySpecifiedGameFromStore_whenDeletingGame() {
        GameId gameId1 = GameId.generate();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        storedGames.put(gameId1, game1);
        GameId gameId2 = GameId.generate();
        Game game2 = new Game("team2", "team3", Score.of(1, 1), Instant.now());
        storedGames.put(gameId2, game2);

        gameRepository.delete(gameId1);

        assertThat(storedGames).hasSize(1)
                               .containsEntry(gameId2, game2);
    }

    @Test
    void shouldNotThrowException_whenDeletingGame_thatDoesNotExist() {
        GameId gameId = GameId.generate();

        assertThatCode(() -> gameRepository.delete(gameId)).doesNotThrowAnyException();
    }

    @Test
    void shouldOverwritePreviousGame_whenUpdatingGame() {
        GameId gameId = GameId.generate();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        storedGames.put(gameId, game1);
        Game game2 = new Game("team2", "team3", Score.of(1, 1), Instant.now());

        gameRepository.update(gameId, game2);

        assertThat(storedGames).hasSize(1)
                               .containsEntry(gameId, game2);
    }

    @Test
    void shouldNotStoreGame_whenUpdatingGame_withNonExistingId() {
        GameId gameId = GameId.generate();
        Game game = new Game("team1", "team2", Score.of(1, 1), Instant.now());

        gameRepository.update(gameId, game);

        assertThat(storedGames).isEmpty();
    }

    @Test
    void shouldReturnOptionalWithGame_whenGettingGame() {
        GameId gameId = GameId.generate();
        Game game = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        storedGames.put(gameId, game);

        Optional<Game> optionalGame = gameRepository.get(gameId);

        assertThat(optionalGame).contains(game);
    }

    @Test
    void shouldReturnEmptyOptional_whenGettingGame_whichDoesNotExist() {
        GameId gameId = GameId.generate();

        Optional<Game> optionalGame = gameRepository.get(gameId);

        assertThat(optionalGame).isEmpty();
    }

    @Test
    void shouldReturnAllStoredGames_whenGettingAllGames() {
        GameId gameId1 = GameId.generate();
        GameId gameId2 = GameId.generate();
        Game game1 = new Game("team1", "team2", Score.of(1, 1), Instant.now());
        Game game2 = new Game("team3", "team4", Score.of(1, 1), Instant.now());
        storedGames.put(gameId1, game1);
        storedGames.put(gameId2, game2);

        Collection<Game> games = gameRepository.getAll();

        assertThat(games).containsExactlyInAnyOrder(game1, game2);
    }

    @Test
    void shouldReturnEmptyList_whenGettingAllGames_andNoneIsPresent() {
        Collection<Game> games = gameRepository.getAll();

        assertThat(games).isEmpty();
    }

}