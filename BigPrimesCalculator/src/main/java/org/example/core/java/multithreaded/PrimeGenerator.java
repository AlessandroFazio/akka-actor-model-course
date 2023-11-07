package org.example.core.java.multithreaded;

import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator implements Runnable {
    private PrimeResults results;

    public PrimeGenerator(PrimeResults results) {
        this.results = results;
    }

    @Override
    public void run() {
        BigInteger bigInteger = new BigInteger(4000, new Random());
        results.addPrime(bigInteger.nextProbablePrime());
    }
}
