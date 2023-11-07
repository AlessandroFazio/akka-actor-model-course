package org.example.core.java.utils;

import org.example.core.java.model.Block;
import org.example.core.java.model.HashResult;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BlockChainUtils {

    public static String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] rawHash = digest.digest(data.getBytes("UTF-8"));
            StringBuffer hextString = new StringBuffer();
            for(int i=0; i < rawHash.length; i++) {
                String hex = Integer.toHexString(0xff & rawHash[i]);
                if(hex.length() == 1) hextString.append('0');
                hextString.append(hex);
            }
            return hextString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static HashResult mineBlock(Block block, int difficultyLevel, int startNonce, int endNonce) {
        String hash = new String(new char[difficultyLevel]).replace("\0", "x");
        String target = new String(new char[difficultyLevel]).replace("\0", "0");

        int nonce = startNonce;
        while(!hash.substring(0,difficultyLevel).equals(target) && nonce < endNonce) {
            nonce++;
            String dataToEncode = block.getPreviousHash() +
                    Long.toString(block.getTransaction().getTimestamp()) +
                    Integer.toString(nonce) + block.getTransaction();

            hash = calculateHash(dataToEncode);
        }

        if(hash.substring(0, difficultyLevel).equals(target)) {
            HashResult hashResult = new HashResult();
            hashResult.foundAHash(hash, nonce);
            return hashResult;
        }
        return null;
    }

    public static boolean validateBlock(Block block) {
        String dataToEncode = block.getPreviousHash() +
                Long.toString(block.getTransaction().getTimestamp()) +
                Integer.toString(block.getNonce()) +
                block.getTransaction();;

        String checkHash = calculateHash(dataToEncode);
        return checkHash.equals(block.getHash());
    }
}
