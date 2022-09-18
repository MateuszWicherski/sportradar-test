package pl.wicherski.sportradar.scoreboard;

import java.util.Objects;
import java.util.UUID;

public class GameId {

    private final UUID id;

    private GameId(UUID id) {
        this.id = id;
    }

    static GameId generate() {
        return new GameId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameId gameId = (GameId) o;
        return Objects.equals(id, gameId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
