package org.example.akka.command;

import akka.actor.typed.ActorRef;

public class NoResponseReceivedCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;
    private ActorRef<WorkerBehaviorCommand> worker;

    public ActorRef<WorkerBehaviorCommand> getWorker() {
        return worker;
    }

    public NoResponseReceivedCommand(ActorRef<WorkerBehaviorCommand> worker) {
        this.worker = worker;
    }
}
