package pl.wicherski.sportradar.scoreboard;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class InMemoryGameRepository implements GameRepository {

    private final Map<GameId, Game> games;

    public InMemoryGameRepository(Map<GameId, Game> games) {
        this.games = games;
    }

    @Override
    public void save(GameId gameId, Game game) {
        games.put(gameId, game);
    }

    @Override
    public void delete(GameId gameId) {
        games.remove(gameId);
    }

    @Override
    public void update(GameId gameId, Game updatedGame) {
        games.replace(gameId, updatedGame);
    }

    @Override
    public Optional<Game> get(GameId gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public Collection<Game> getAll() {
        return games.values();
    }

}
