package org.example.akka.behavior;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.example.akka.command.racer.PositionCommand;
import org.example.akka.command.racer.RacerCommand;
import org.example.akka.command.racer.RacerStartCommand;
import org.example.akka.command.racercontroller.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaceControllerBehavior extends AbstractBehavior<RacerControllerCommand> {

    public static Behavior<RacerControllerCommand> create() {
        return Behaviors.setup(RaceControllerBehavior::new);
    }

    private Map<ActorRef<RacerCommand>, Integer> currentPositions;
    private Map<ActorRef<RacerCommand>, Long> finishingTimes;

    private RaceControllerBehavior(ActorContext<RacerControllerCommand> context) {
        super(context);
    }
    private long start;
    private final int raceLength = 100;

    private Object TIMER_KEY;
    private final int numRacers = 10;

    private void handleOnStartCommand() {
        start = System.currentTimeMillis();
        currentPositions = new HashMap<>();
        finishingTimes = new HashMap<>();
        for(int i=0; i < numRacers; i++) {
            ActorRef<RacerCommand> racer = getContext().spawn(
                    RacerBehavior.create(),
                    String.format("racer-%d", i));
            currentPositions.put(racer, 0);
            racer.tell(new RacerStartCommand(raceLength));
        }
    }

    private void displayRace() {
        int displayLength = 100;
        for(int i=0; i < 50; i++) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds. ");
        System.out.println("    " + new String(new char[displayLength]).replace('\0', '='));
        int i = 0;
        for(ActorRef<RacerCommand> racer: currentPositions.keySet()) {
            System.out.println(i + " : " + new String(new char[currentPositions.get(racer) * displayLength / 100]).replace('\0', '='));
            i++;
        }
    }

    @Override
    public Receive<RacerControllerCommand> createReceive() {
        return raceNotYetStartedHandler();
    }

    public Receive<RacerControllerCommand> raceNotYetStartedHandler() {
        return newReceiveBuilder()
                .onMessage(ControllerStartCommand.class, message -> {
                    handleOnStartCommand();
                    return Behaviors.withTimers(timers -> {
                        timers.startTimerAtFixedRate(TIMER_KEY, new GetPositionCommand(),
                                Duration.of(1, ChronoUnit.SECONDS));
                        return Behaviors.same();
                    });
                })
                .onMessage(GetPositionCommand.class, message -> {

                    for(ActorRef<RacerCommand> racer: currentPositions.keySet()) {
                        racer.tell(new PositionCommand(getContext().getSelf()));
                    }
                    displayRace();

                    return Behaviors.same();
                })
                .onMessage(RacerUpdateCommand.class, message -> {

                    RacerUpdateCommand racerUpdateCommand = (RacerUpdateCommand) message;
                    currentPositions.put(
                            racerUpdateCommand.getRacerRef(),
                            racerUpdateCommand.getCurrentPosition()
                    );

                    return Behaviors.same();
                })
                .onMessage(RacerFinishedCommand.class, message -> {
                    finishingTimes.put(message.getRacer(), System.currentTimeMillis());
                    if(finishingTimes.size() == numRacers) return raceCompletedHandler();
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<RacerControllerCommand> raceCompletedHandler() {
        return newReceiveBuilder()
                .onMessage(GetPositionCommand.class, message -> {
                    finishingTimes.keySet().forEach(racer -> getContext().stop(racer));
                    displayResults();
                    return Behaviors.withTimers(timers -> {
                        timers.cancelAll();
                        return Behaviors.stopped();
                    });
                })
                .build();
    }

    private void displayResults() {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        System.out.println("Results");
        finishingTimes.values().stream().sorted().forEach(it -> {
            for(ActorRef<RacerCommand> racer: finishingTimes.keySet()) {
                if(Objects.equals(finishingTimes.get(racer), it)) {
                    Matcher matcher = pattern.matcher(racer.path().toString());
                    StringBuilder builder = new StringBuilder();
                    while (matcher.find()) {
                        builder.append(matcher.group());
                    }
                    String racerId = builder.toString();
                    System.out.println("Racer" + racerId + " finished in " + ( (double)it - start) / 1000 + " seconds. ");
                }
            }
        });
    }
}
