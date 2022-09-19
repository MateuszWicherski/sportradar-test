package pl.wicherski.sportradar.scoreboard;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ThreadSafeLiveScoreBoard implements LiveScoreBoard {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final LiveScoreBoard liveScoreBoard;

    ThreadSafeLiveScoreBoard(LiveScoreBoard liveScoreBoard) {
        this.liveScoreBoard = liveScoreBoard;
    }

    @Override
    public GameId startGame(String homeTeam, String awayTeam) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return liveScoreBoard.startGame(homeTeam, awayTeam);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void updateScore(GameId gameId, Score score) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            liveScoreBoard.updateScore(gameId, score);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void finishGame(GameId gameId) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            liveScoreBoard.finishGame(gameId);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public ScoreSummary getSummary() {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return liveScoreBoard.getSummary();
        } finally {
            readLock.unlock();
        }
    }

}
