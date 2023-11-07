package org.example.akka.command;


import akka.actor.typed.ActorRef;

import java.math.BigInteger;
import java.util.SortedSet;

public class InstructionCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;
    private String message;
    private ActorRef<SortedSet<BigInteger>> sender;

    public ActorRef<SortedSet<BigInteger>> getSender() {
        return sender;
    }

    public InstructionCommand(String message, ActorRef<SortedSet<BigInteger>> sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
}
