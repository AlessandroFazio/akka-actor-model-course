package org.example.akka.blockchain;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import org.example.akka.exception.BlockValidationException;
import org.example.akka.model.Block;
import org.example.akka.model.BlockChain;
import org.example.akka.model.HashResult;
import org.example.akka.utils.BlocksData;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

public class BlockChainMiner {
    int difficultyLevel = 5;
    BlockChain blocks = new BlockChain();
    long start = System.currentTimeMillis();
    ActorSystem<ControllerBehavior.Command> actorSystem;

    private void mineNextBlock() {
        int nextBlockId = blocks.getSize();
        if(nextBlockId < 10) {
            String lastHash = nextBlockId > 0 ? blocks.getLastHash() : "0";
            Block block = BlocksData.getNextBlock(nextBlockId, lastHash);
            CompletionStage<HashResult> results = AskPattern.ask(
                    actorSystem,
                    me -> new ControllerBehavior.MineBlockCommand(block, me, difficultyLevel),
                    Duration.of(30, ChronoUnit.SECONDS),
                    actorSystem.scheduler());

            results.whenComplete((reply, failure) -> {
                if(reply == null || reply.isComplete()) {
                    System.out.println("ERROR: No valid hash found for a block");
                }

                block.setHash(reply.getHash());
                block.setNonce(reply.getNonce());

                try {
                    blocks.addBlock(block);
                    System.out.println("Block added with hash : " + block.getHash());
                    System.out.println("Block added with nonce : " + block.getNonce());
                    mineNextBlock();
                } catch (BlockValidationException e) {
                    System.out.println("ERROR: No valid hash was found for a block");
                }
            });
        }
        else {
            long end = System.currentTimeMillis();
            actorSystem.terminate();
            blocks.printAndValidate();
            System.out.println("Time taken " + (end - start) + " ms.");
        }
    }

    public void mineAnIndipendentBlock() {
        Block block = BlocksData.getNextBlock(7, "123456");
        CompletionStage<HashResult> results = AskPattern.ask(
                actorSystem,
                me -> new ControllerBehavior.MineBlockCommand(block, me, difficultyLevel),
                Duration.of(30, ChronoUnit.SECONDS),
                actorSystem.scheduler());

        results.whenComplete((reply, failure) -> {
            if(reply == null) {
                System.out.println("ERROR: No hash found");
            }
            else {
                System.out.println("Everything is ok");
            }
        });
    }

    public void mineBlocks() {
        actorSystem = ActorSystem.create(MiningSystemBehavior.create(), "MiningSystemBehavior");
        mineNextBlock();
        mineAnIndipendentBlock();
    }
}
