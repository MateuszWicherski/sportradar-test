# Football World Cup Score Board - interview task

## Purpose and requirements

You are working on a sports data company. And we would like you to develop a new
Live Football World Cup Score Board that shows matches and scores.
The boards support the following operations:

1. Start a game. When a game starts, it should capture (being initial score 0-0)
   a) Home team
   b) Away Team
2. Finish a game. It will remove a match from the scoreboard.
3. Update score. Receiving the pair score; home team score and away team score
   updates a game score
4. Get a summary of games by total score. Those games with the same total score
   will be returned ordered by the most recently added to our system.
   As an example, being the current data in the system:
   a) Mexico - Canada: 0 – 5
   b) Spain - Brazil: 10 – 2
   c) Germany - France: 2 – 2
   d) Uruguay - Italy: 6 – 6
   e) Argentina - Australia: 3 - 1
   The summary would provide with the following information:
    1. Uruguay 6 - Italy 6
    2. Spain 10 - Brazil 2
    3. Mexico 0 - Canada 5
    4. Argentina 3 - Australia 1
    5. Germany 2 - France 2

## Developer notes

- I personally don't use 'book' TDD very often, as it does not suit my though process. I do drive my development by
  tests - for me clean code = readable and testable, so I tend to build my code in a way, that allows me to test it
  easily.

## Usage

To use the library, it is needed to obtain `LiveScoreBoardFactory` instance by using its static `getInstance()` method.
Then, two implementations of `LiveScoreBoard` can be produced - one thread unsafe (faster) with `newBoard()` method and
another, thread-safe one, with method `newThreadSafeBoard()`.

`LiveScoreBoard` exposes APIs for:

- starting a game with `startGame(String, String)` method
- changing the game score with `updateScore(GameId, Score)` method
- finalizing the game with `finalizeGame(GameId)` method
- producing a score summary of currently tracked games with `getSummary()` method

Code example of library usage:

```java
import pl.wicherski.sportradar.scoreboard.*;

class Main {

    public static void main(String[] args) {
        // initialize board
        LiveScoreBoardFactory factory = LiveScoreBoardFactory.getInstance();
        LiveScoreBoard board = factory.newBoard();

        // start games
        GameId mexicoCanadaGameId = board.startGame("Mexico", "Canada");
        board.updateScore(mexicoCanadaGameId, Score.of(0, 5));
        GameId spainBrazilGameId = board.startGame("Spain", "Brazil");
        board.updateScore(spainBrazilGameId, Score.of(10, 2));
        GameId germanyFranceGameId = board.startGame("Germany", "France");
        board.updateScore(germanyFranceGameId, Score.of(2, 2));
        GameId uruguayItalyGameId = board.startGame("Uruguay", "Italy");
        board.updateScore(uruguayItalyGameId, Score.of(6, 6));
        GameId argentinaAustraliaGameId = board.startGame("Argentina", "Australia");
        board.updateScore(argentinaAustraliaGameId, Score.of(3, 1));

        // check summary
        ScoreSummary summary = board.getSummary();
        /* summary.toPrintableSummary() -> 
             1. Uruguay 6 - Italy 6
             2. Spain 10 - Brazil 2
             3. Mexico 0 - Canada 5
             4. Argentina 3 - Australia 1
             5. Germany 2 - France 2""");
         */

        // finish some games
        board.finishGame(uruguayItalyGameId);
        board.finishGame(spainBrazilGameId);
        // update some games
        board.updateScore(mexicoCanadaGameId, Score.of(3, 6));

        // check summary after finishing and updating games
        ScoreSummary newSummary = board.getSummary();
        /* newSummary.toPrintableSummary() -> 
             1. Mexico 3 - Canada 6
             2. Argentina 3 - Australia 1
             3. Germany 2 - France 2""");
         */
    }

}
```