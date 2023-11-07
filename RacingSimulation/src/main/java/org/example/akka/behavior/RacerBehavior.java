package org.example.akka.behavior;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.PostStop$;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.example.akka.command.racer.PositionCommand;
import org.example.akka.command.racer.RacerCommand;
import org.example.akka.command.racer.RacerStartCommand;
import org.example.akka.command.racercontroller.RacerFinishedCommand;
import org.example.akka.command.racercontroller.RacerUpdateCommand;

import java.util.Random;

public class RacerBehavior extends AbstractBehavior<RacerCommand> {
    private final double defaultAverageSpeed = 48.2;
    private int averageSpeedAdjustmentFactor;
    private Random random;

    private double currentSpeed = 0;

    public static Behavior<RacerCommand> create() {
        return Behaviors.setup(RacerBehavior::new);
    }

    private RacerBehavior(ActorContext<RacerCommand> context) {
        super(context);
    }

    @Override
    public Receive<RacerCommand> createReceive() {
        return notYetStartedHandler();
    }

    public Receive<RacerCommand> notYetStartedHandler() {
        return newReceiveBuilder()
                .onMessage(RacerStartCommand.class, message -> {
                    random = new Random();
                    averageSpeedAdjustmentFactor = random.nextInt(30) - 10;
                    return runningHandler(message.getRaceLength(), 0D);
                })
                .onMessage(PositionCommand.class, message -> {
                    message.getControllerRef().tell(
                            new RacerUpdateCommand(getContext().getSelf(),0));
                    return Behaviors.same();
                })
                .build();
    }

    public Receive<RacerCommand> runningHandler(int raceLength, double currentPosition) {
        return newReceiveBuilder()
                .onMessage(PositionCommand.class, message -> {
                    double newPosition = currentPosition;

                    determineNextSpeed(newPosition, raceLength);
                    newPosition += getDistanceMovedPerSecond();
                    if(newPosition > raceLength) newPosition = raceLength;

                    message.getControllerRef().tell(
                            new RacerUpdateCommand(getContext().getSelf(),(int) newPosition));

                    if(newPosition == raceLength) return raceCompletedHandler(raceLength);
                    return runningHandler(raceLength, newPosition);
                })
                .build();
    }

    public Receive<RacerCommand> raceCompletedHandler(int raceLength) {
        return newReceiveBuilder()
                .onMessage(PositionCommand.class, message -> {
                    message.getControllerRef().tell(
                            new RacerUpdateCommand(getContext().getSelf(), raceLength));
                    message.getControllerRef().tell(new RacerFinishedCommand(getContext().getSelf()));
                    return waitStopHandler();
                })
                .build();
    }

    public Receive<RacerCommand> waitStopHandler() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    return Behaviors.same();
                })
                .onSignal(PostStop.class, signal -> {
                    if(getContext().getLog().isInfoEnabled()) {
                        getContext().getLog().info("I'm about to terminate!");
                    }
                    return Behaviors.same();
                })
                .build();
    }

    private double getMaxSpeed() {
        return defaultAverageSpeed * (1 + ((double) averageSpeedAdjustmentFactor / 100 ));
    }

    private double getDistanceMovedPerSecond() {
        return currentSpeed * 1000 / 3600;
    }

    private void determineNextSpeed(double currentPosition, int raceLength) {
        if(currentPosition < ((double) raceLength / 4)) {
            currentSpeed += ((getMaxSpeed() - currentSpeed) / 10) * random.nextDouble();
        }
        else {
            currentSpeed *= 0.5 + random.nextDouble();
        }

        if(currentSpeed > getMaxSpeed())
            currentSpeed = getMaxSpeed();

        if(currentSpeed < 5)
            currentSpeed = 5;

        if(currentSpeed > ((double) raceLength / 2) && currentSpeed < getMaxSpeed() / 2) {
            currentSpeed = getMaxSpeed() / 2;
        }
    }
}
