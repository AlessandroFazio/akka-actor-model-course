package org.example.akka.model;

public class Transaction {
    private int id;
    private long Timestamp;
    private int accountNumber;
    private double amount;

    public Transaction(int id, long timestamp, int accountNumber, double amount) {
        super();
        this.id = id;
        Timestamp = timestamp;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", Timestamp=" + Timestamp +
                ", accountNumber=" + accountNumber +
                ", amount=" + amount +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
