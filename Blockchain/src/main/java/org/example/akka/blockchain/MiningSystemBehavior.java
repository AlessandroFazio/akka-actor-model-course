package org.example.akka.blockchain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.*;

public class MiningSystemBehavior extends AbstractBehavior<ControllerBehavior.Command> {

    private PoolRouter<ControllerBehavior.Command> controllerPoolRouter;
    private ActorRef<ControllerBehavior.Command> controllers;

    public static Behavior<ControllerBehavior.Command> create() {
        return Behaviors.setup(MiningSystemBehavior::new);
    }
    private MiningSystemBehavior(ActorContext<ControllerBehavior.Command> context) {
        super(context);
        controllerPoolRouter = Routers.pool(3,
                Behaviors.supervise(ControllerBehavior.create()).onFailure(SupervisorStrategy.restart()));
        controllers = getContext().spawn(controllerPoolRouter, "controllerRouter");
    }

    @Override
    public Receive<ControllerBehavior.Command> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    controllers.tell(message);
                    return Behaviors.same();
                })
                .build();
    }
}
