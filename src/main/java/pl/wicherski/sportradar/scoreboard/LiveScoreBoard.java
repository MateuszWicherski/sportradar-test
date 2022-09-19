package pl.wicherski.sportradar.scoreboard;

/**
 * Interface for score board operations.
 */
public interface LiveScoreBoard {

    /**
     * Starts tracking a game between two teams - home and away. The game will be initialized with score 0-0.
     *
     * @param homeTeam - home team name
     * @param awayTeam - away team name
     * @return ID of the newly created game
     */
    GameId startGame(String homeTeam, String awayTeam);

    /**
     * Finishes a game with provided ID. If the game ID is not recognizable or points to the already finished game,
     * then nothing will happen.
     * Finished game will not be tracked anymore.
     *
     * @param gameId - ID of the game to finish.
     */
    void finishGame(GameId gameId);

    /**
     * Updates a score of the tracked game.
     *
     * @param gameId - ID of the game to update score
     * @param score  - new score of the game
     * @throws GameNotFoundException if game with given ID does not exist or is already finished.
     */
    void updateScore(GameId gameId, Score score) throws GameNotFoundException;

    /**
     * Returns a summary of all tracked games.
     *
     * @return summary of all tracked games
     */
    ScoreSummary getSummary();

}
