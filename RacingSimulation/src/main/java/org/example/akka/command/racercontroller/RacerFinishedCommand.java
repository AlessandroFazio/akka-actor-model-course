package org.example.akka.command.racercontroller;

import akka.actor.typed.ActorRef;
import org.example.akka.command.racer.RacerCommand;

public class RacerFinishedCommand implements RacerControllerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<RacerCommand> racer;

    public RacerFinishedCommand(ActorRef<RacerCommand> racer) {
        this.racer = racer;
    }

    public ActorRef<RacerCommand> getRacer() {
        return racer;
    }
}
