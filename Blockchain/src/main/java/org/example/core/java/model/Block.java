package org.example.core.java.model;

public class Block {
    private String previousHash;
    private Transaction transaction;
    private int nonce;
    private String hash;

    public Block(String previousHash, Transaction transaction) {
        this.previousHash = previousHash;
        this.transaction = transaction;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
