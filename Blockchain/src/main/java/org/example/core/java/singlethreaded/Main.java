package org.example.core.java.singlethreaded;

import org.example.core.java.model.Block;
import org.example.core.java.model.BlockChain;
import org.example.core.java.model.HashResult;
import org.example.core.java.utils.BlockChainUtils;
import org.example.core.java.utils.BlocksData;

public class Main {
    public static void main(String[] args) {
        int difficultyLevel = 5;

        Long start = System.currentTimeMillis();
        BlockChain blocks = new BlockChain();

        String lastHash = "0";
        for(int i=0; i < 10; i++) {
            Block nextBlock = BlocksData.getNextBlock(i, lastHash);

            HashResult hashResult = BlockChainUtils.mineBlock(nextBlock, difficultyLevel, 0, 1000000000);
            if(hashResult == null) throw new RuntimeException("Didn't find a valid hash for block " + i);

            nextBlock.setHash(hashResult.getHash());
            nextBlock.setNonce(hashResult.getNonce());
            blocks.addBlock(nextBlock);
            System.out.println("Block " + i + " hash : " + nextBlock.getHash());
            System.out.println("Block " + i + " nonce : " + nextBlock.getNonce());
            lastHash = nextBlock.getHash();
        }

        Long end = System.currentTimeMillis();
        blocks.printAndValidate();

        System.out.println("Time taken " + (end - start) + " ms.");
    }
}
