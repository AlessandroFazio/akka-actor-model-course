package org.example.akka.blockchain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;
import org.example.akka.model.Block;
import org.example.akka.model.HashResult;

import java.io.Serializable;
import java.util.Objects;

public class ControllerBehavior extends AbstractBehavior<ControllerBehavior.Command> {
    private static int numWorkers = Runtime.getRuntime().availableProcessors() - 2;
    private StashBuffer<Command> stashBuffer;

    public interface Command extends Serializable {
    }

    public static class MineBlockCommand implements Command {
        private static final long serialVersionUID = 1L;
        private Block block;
        private ActorRef<HashResult> sender;
        private int difficulty;

        public Block getBlock() {
            return block;
        }

        public ActorRef<HashResult> getSender() {
            return sender;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public MineBlockCommand(Block block, ActorRef<HashResult> sender, int difficulty) {
            this.block = block;
            this.sender = sender;
            this.difficulty = difficulty;
        }
    }

    public static class HashResultCommand implements Command {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HashResultCommand that = (HashResultCommand) o;
            return Objects.equals(hashResult, that.hashResult);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hashResult);
        }

        private static final long serialVersionUID = 1L;
        private HashResult hashResult;

        public HashResult getHashResult() {
            return hashResult;
        }

        public HashResultCommand(HashResult hashResult) {
            this.hashResult = hashResult;
        }
    }

    public static Behavior<Command> create() {
        return Behaviors.withStash(10, stash -> {
            return Behaviors.setup(context -> {
                    return new ControllerBehavior(context, stash);
            });
        });
    }

    private ControllerBehavior(ActorContext<Command> context, StashBuffer<Command> stashBuffer) {
        super(context);
        this.stashBuffer = stashBuffer;
    }

    @Override
    public Receive<Command> createReceive() {
        return idleMessageHandler();
    }

    public Receive<Command> idleMessageHandler() {
        return newReceiveBuilder()
                .onSignal(Terminated.class, message -> Behaviors.same())
                .onMessage(MineBlockCommand.class, message -> {
                    this.currentlyMining = true;
                    this.sender = message.getSender();
                    this.block = message.getBlock();
                    this.difficulty = message.getDifficulty();
                    for(int i=0; i < numWorkers; i++) startNextWorker();
                    return activeMessageHandler();
                })
                .build();
    }

    public Receive<Command> activeMessageHandler() {
        return newReceiveBuilder()
                .onSignal(Terminated.class, handler -> {
                    startNextWorker();
                    return Behaviors.same();
                })
                .onMessage(HashResultCommand.class, message -> {
                    getContext().getChildren()
                            .forEach(childActor -> getContext().stop(childActor));

                    this.currentlyMining = false;
                    sender.tell(message.getHashResult());
                    return stashBuffer.unstashAll(idleMessageHandler());
                })
                .onMessage(MineBlockCommand.class, message -> {
                    System.out.println("Delaying a mining request");
                    if(!stashBuffer.isFull()) stashBuffer.stash(message);
                    return Behaviors.same();
                })
                .build();
    }

    private ActorRef<HashResult> sender;
    private Block block;
    private int difficulty;
    private int currentNonce = 0;
    private boolean currentlyMining;

    private void startNextWorker() {
        if(!currentlyMining) return;

        // System.out.println("About to start mining with nonces starting at " + currentNonce * 1000);

        Behavior<WorkerBehavior.Command> workerBehavior = Behaviors.supervise(WorkerBehavior.create())
                .onFailure(SupervisorStrategy.resume());

        ActorRef<WorkerBehavior.Command> worker =
                getContext().spawn(workerBehavior, "worker-" + currentNonce);

        getContext().watch(worker);

        worker.tell(new WorkerBehavior.Command(
                block, currentNonce * 1000, difficulty, getContext().getSelf()));
        currentNonce++;
    }
}
