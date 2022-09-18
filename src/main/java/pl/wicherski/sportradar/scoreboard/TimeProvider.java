package pl.wicherski.sportradar.scoreboard;

import java.time.Instant;

interface TimeProvider {

    Instant now();

}
