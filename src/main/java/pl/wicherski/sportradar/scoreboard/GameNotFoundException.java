package pl.wicherski.sportradar.scoreboard;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(GameId gameId) {
        super("Game with ID %s was not found!".formatted(gameId));
    }

}
