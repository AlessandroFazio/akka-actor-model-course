package org.example.akka.blockchain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.example.akka.model.Block;
import org.example.akka.model.HashResult;

import java.util.Random;

import static org.example.akka.utils.BlockChainUtils.calculateHash;

public class WorkerBehavior extends AbstractBehavior<WorkerBehavior.Command> {
    public static final int workerNoncePartitionSize = 1000;
    public static class Command {
        private Block block;
        private int startNonce;
        private int difficulty;
        private ActorRef<ControllerBehavior.Command> controller;

        public Command(Block block, int startNonce, int difficulty, ActorRef<ControllerBehavior.Command> controller) {
            this.block = block;
            this.startNonce = startNonce;
            this.difficulty = difficulty;
            this.controller = controller;
        }

        public Block getBlock() {
            return block;
        }

        public ActorRef<ControllerBehavior.Command> getController() {
            return controller;
        }
        public int getStartNonce() {
            return startNonce;
        }
        public int getDifficulty() {
            return difficulty;
        }
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(WorkerBehavior::new);
    }
    private WorkerBehavior(ActorContext<WorkerBehavior.Command> context) {
        super(context);
    }

    @Override
    public Receive<WorkerBehavior.Command> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    String hash = new String(new char[message.getDifficulty()]).replace("\0", "x");
                    String target = new String(new char[message.getDifficulty()]).replace("\0", "0");

                    int nonce = message.getStartNonce();
                    while(!hash.substring(0, message.getDifficulty()).equals(target)
                            && nonce < message.getStartNonce() + workerNoncePartitionSize) {
                        nonce++;
                        String dataToEncode = message.getBlock().getPreviousHash() +
                                Long.toString(message.getBlock().getTransaction().getTimestamp()) +
                                Integer.toString(nonce) + message.getBlock().getTransaction();

                        hash = calculateHash(dataToEncode);
                    }

                    if(hash.substring(0, message.getDifficulty()).equals(target)) {
                        HashResult hashResult = new HashResult();
                        hashResult.foundAHash(hash, nonce);

                        // Log for UNIT test the WorkerBehavior
                        getContext().getLog().debug(
                                hashResult.getNonce() + " : " + hashResult.getHash()
                        );
                        message.getController().tell(
                                new ControllerBehavior.HashResultCommand(hashResult));
                        return Behaviors.same();
                    }
                    // Log for UNIT test the WorkerBehavior
                    getContext().getLog().debug("null");
                    Random r = new Random();
                    // if(r.nextInt(10) == 3) throw new ArithmeticException("No hash found");
                    return Behaviors.stopped();
                })
                .build();
    }
}
