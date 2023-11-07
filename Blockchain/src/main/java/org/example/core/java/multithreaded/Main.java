package org.example.core.java.multithreaded;

import org.example.akka.model.BlockChain;
import org.example.akka.model.HashResult;
import org.example.core.java.utils.BlocksData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

    }
}
    /*
    public static void main(String[] args) {
        int difficultyLevel = 5;

        long start = System.currentTimeMillis();
        BlockChain blocks = new BlockChain();

        String lastHash = "0";
        int numCores = Runtime.getRuntime().availableProcessors();
        for(int i=0; i < numCores; i++) {
            ExecutorService executor = Executors.newFixedThreadPool(numCores);
            Block nextBlock = BlocksData.getNextBlock(i, lastHash);
            HashResult hashResult = new HashResult();
            Thread resultsThread = new Thread(new CheckForResults(hashResult));
            resultsThread.start();

            for(int nonceSeed = 0; nonceSeed < 1000; nonceSeed++) {
                BlockChainMiner miner = new BlockChainMiner(nextBlock, nonceSeed * 1000, hashResult, difficultyLevel);
                executor.execute(miner);
            }

            try {
                resultsThread.join();
                executor.shutdown();
                if(!hashResult.isComplete()) {

                }
            }
        }
    }
}
 */