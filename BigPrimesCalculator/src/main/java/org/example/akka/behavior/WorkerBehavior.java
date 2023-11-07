package org.example.akka.behavior;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.example.akka.command.BigPrimeCommand;
import org.example.akka.command.WorkerBehaviorCommand;
import org.example.akka.exception.WrongMessageException;

import java.math.BigInteger;
import java.util.Random;

public class WorkerBehavior extends AbstractBehavior<WorkerBehaviorCommand> {

    public static Behavior<WorkerBehaviorCommand> create() {
        return Behaviors.setup(WorkerBehavior::new);
    }

    @Override
    public Receive<WorkerBehaviorCommand> createReceive() {
        return beforeGettingPrimeHandler();
    }

    private WorkerBehavior(ActorContext<WorkerBehaviorCommand> context) {
        super(context);
    }

    private void handleCalculatedPrimeCommand(WorkerBehaviorCommand message, BigInteger prime) {
        if (message.getMessage().equals("start")) {
            System.out.println("actor: " + getContext().getSelf().path() + " found prime: " + prime);
            message.getSender().tell(new BigPrimeCommand(prime));
        }
    }

    public Receive<WorkerBehaviorCommand> beforeGettingPrimeHandler() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    Random r = new Random();
                    BigInteger prime = null;
                    if (message.getMessage().equals("start")) {
                        prime = new BigInteger(3750, new Random())
                                .nextProbablePrime();

                        System.out.println("actor: " + getContext().getSelf().path() + " found prime: " + prime);
                        if(r.nextInt(5) < 2) {
                            message.getSender().tell(new BigPrimeCommand(prime));
                        }
                    }
                    else {
                        throw new WrongMessageException(String.format("The message value should be start, but got %s", message.getMessage()));
                    }
                    return afterGotPrimeHandler(prime);
                })
                .build();
    }

    public Receive<WorkerBehaviorCommand> afterGotPrimeHandler(BigInteger prime) {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    handleCalculatedPrimeCommand(message, prime);
                    return Behaviors.same();
                })
                .build();
    }
}
