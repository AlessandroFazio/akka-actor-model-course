package org.example.core.java.singlethreaded;

import java.math.BigInteger;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class SingleThreadedBigPrimes {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        SortedSet<BigInteger> primes = new TreeSet<>();

        while (primes.size() < 20) {
            BigInteger bigInteger = new BigInteger(4000, new Random());
            primes.add(bigInteger.nextProbablePrime());
        }

        long end = System.currentTimeMillis();
        System.out.println("Primes size: " + primes.size());
        System.out.println("Time taken was " + (end - start) + " ms. ");
    }
}
