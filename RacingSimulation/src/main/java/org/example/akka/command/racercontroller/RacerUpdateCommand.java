package org.example.akka.command.racercontroller;

import akka.actor.typed.ActorRef;
import org.example.akka.command.racer.RacerCommand;

public class RacerUpdateCommand implements RacerControllerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<RacerCommand> racerRef;
    private int currentPosition;

    public ActorRef<RacerCommand> getRacerRef() {
        return racerRef;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public RacerUpdateCommand(ActorRef<RacerCommand> racerRef, int currentPosition) {
        this.racerRef = racerRef;
        this.currentPosition = currentPosition;
    }
}
