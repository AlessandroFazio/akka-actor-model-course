package org.example.akka;

import akka.actor.typed.ActorSystem;
import org.example.akka.behavior.RaceControllerBehavior;
import org.example.akka.command.racercontroller.ControllerStartCommand;
import org.example.akka.command.racercontroller.RacerControllerCommand;

public class Main {
    public static void main(String[] args) {
        ActorSystem<RacerControllerCommand> racerController = ActorSystem.create(
                RaceControllerBehavior.create(), "RaceSimulation");
        racerController.tell(new ControllerStartCommand());
    }
}
