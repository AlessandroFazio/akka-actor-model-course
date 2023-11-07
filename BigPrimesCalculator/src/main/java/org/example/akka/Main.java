package org.example.akka;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import org.example.akka.behavior.ManagerBehavior;
import org.example.akka.command.InstructionCommand;
import org.example.akka.command.ManagerCommand;

import java.math.BigInteger;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.SortedSet;
import java.util.concurrent.CompletionStage;

public class Main {
    public static void main(String[] args) {
        ActorSystem<ManagerCommand> bigPrimes = ActorSystem.create(ManagerBehavior.create(), "BigPrimes");

        int timeoutSeconds = 20;
        CompletionStage<SortedSet<BigInteger>> primesResult =
                AskPattern.ask(
                    bigPrimes,
                    me -> new InstructionCommand("start", me),
                    Duration.of(timeoutSeconds, ChronoUnit.SECONDS),
                    bigPrimes.scheduler());

        primesResult.whenComplete(
                (reply, failure) -> {
                    if(reply != null) reply.forEach(System.out::println);
                    else System.out.println("The system didn't respond in time.");
                    bigPrimes.terminate();
                }
        );
    }
}