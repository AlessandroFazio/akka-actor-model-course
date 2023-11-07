package org.example.akka.command.racer;

import akka.actor.typed.ActorRef;
import org.example.akka.command.racercontroller.RacerControllerCommand;

public class PositionCommand implements RacerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<RacerControllerCommand> controllerRef;

    public PositionCommand(ActorRef<RacerControllerCommand> controllerRef) {
        this.controllerRef = controllerRef;
    }

    public ActorRef<RacerControllerCommand> getControllerRef() {
        return controllerRef;
    }
}
