package pl.wicherski.sportradar.scoreboard;

import java.util.Collection;
import java.util.Optional;

interface GameRepository {

    void save(GameId gameId, Game game);

    void delete(GameId gameId);

    void update(GameId gameId, Game updatedGame);

    Optional<Game> get(GameId gameId);

    Collection<Game> getAll();

}
