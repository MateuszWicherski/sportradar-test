package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveScoreBoardIntegrationTest {

    @Test
    void scoreBoardIntegrationFlowTest() {
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
        assertThat(summary.toPrintableSummary()).isEqualTo("""
                                                           1. Uruguay 6 - Italy 6
                                                           2. Spain 10 - Brazil 2
                                                           3. Mexico 0 - Canada 5
                                                           4. Argentina 3 - Australia 1
                                                           5. Germany 2 - France 2""");

        // finish some games
        board.finishGame(uruguayItalyGameId);
        board.finishGame(spainBrazilGameId);
        // update some games
        board.updateScore(mexicoCanadaGameId, Score.of(3, 6));

        // check summary after finishing and updating games
        ScoreSummary newSummary = board.getSummary();
        assertThat(newSummary.toPrintableSummary()).isEqualTo("""
                                                              1. Mexico 3 - Canada 6
                                                              2. Argentina 3 - Australia 1
                                                              3. Germany 2 - France 2""");

        // verify previous summary object is unchanged
        assertThat(summary.toPrintableSummary()).isEqualTo("""
                                                           1. Uruguay 6 - Italy 6
                                                           2. Spain 10 - Brazil 2
                                                           3. Mexico 0 - Canada 5
                                                           4. Argentina 3 - Australia 1
                                                           5. Germany 2 - France 2""");
    }

}
