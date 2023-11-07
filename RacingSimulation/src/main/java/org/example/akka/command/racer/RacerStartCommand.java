package org.example.akka.command.racer;

public class RacerStartCommand implements RacerCommand {
    private static final long serialVersionUID = 1L;
    private int raceLength;

    public int getRaceLength() {
        return raceLength;
    }

    public RacerStartCommand(int raceLength) {
        this.raceLength = raceLength;
    }
}
