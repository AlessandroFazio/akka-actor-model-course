package org.example.akka.behavior;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.example.akka.command.*;

import java.math.BigInteger;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ManagerBehavior extends AbstractBehavior<ManagerCommand> {
    private static final SortedSet<BigInteger> primes = new TreeSet<>();
    private static final int numWorkers = 20;
    private ActorRef<SortedSet<BigInteger>> sender;

    public static Behavior<ManagerCommand> create() {
        return Behaviors.setup(ManagerBehavior::new);
    }

    private ManagerBehavior(ActorContext<ManagerCommand> context) {
        super(context);
    }

    private void createWorkers() {
        for (int i = 0; i < numWorkers; i++) {
            ActorRef<WorkerBehaviorCommand> worker = getContext().spawn(
                    WorkerBehavior.create(),
                    String.format("worker-%d", i));
            askWorkerForAPrime(worker);
        }
    }

    private void handleInstructionCommand(ManagerCommand message) {
        String messageValue = ((InstructionCommand) message).getMessage();
        if(messageValue.equals("start")) createWorkers();
    }

    private void handlePrimeCommand(ManagerCommand message) {
        primes.add(((BigPrimeCommand) message).getMessage());
        if(primes.size() == 20)
            primes.forEach(System.out::println);
    }

    @Override
    public Receive<ManagerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, message -> {
                    handleInstructionCommand(message);
                    sender = message.getSender();
                    return Behaviors.same();
                })
                .onMessage(BigPrimeCommand.class, message -> {
                    handlePrimeCommand(message);
                    if(primes.size() == 20) sender.tell(primes);
                    return Behaviors.same();
                })
                .onMessage(NoResponseReceivedCommand.class, message -> {
                    System.out.println("Retrying with worker " + message.getWorker().path());
                    askWorkerForAPrime(message.getWorker());
                    return Behaviors.same();
                })
                .build();
    }

    private void askWorkerForAPrime(ActorRef<WorkerBehaviorCommand> worker) {
        getContext().ask(
                ManagerCommand.class,
                worker,
                Duration.of(10, ChronoUnit.SECONDS),
                me -> new WorkerBehaviorCommand("start", me),
                (response, throwable) -> {
                    if(response != null) {
                        return response;
                    }
                    else {
                        System.out.println("Worker " + worker.path() + " failed to respond");
                        return new NoResponseReceivedCommand(worker);
                    }
                });
    }
}