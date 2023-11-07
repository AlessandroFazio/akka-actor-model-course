package org.example.akka.command;

import akka.actor.typed.ActorRef;

import java.io.Serializable;

// No SETTERS -> immutable fields after construction
public class WorkerBehaviorCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private ActorRef<ManagerCommand> sender;

    public WorkerBehaviorCommand(String message, ActorRef<ManagerCommand> sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public ActorRef<ManagerCommand> getSender() {
        return sender;
    }
}
