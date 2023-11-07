package org.example.core.java.multithreaded;

import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class PrimeResults {
    private SortedSet<BigInteger> primes;

    public PrimeResults() {
        primes = new TreeSet<>();
    }

    public int getSize() {
        synchronized (this) {
            return primes.size();
        }
    }

    public void addPrime(BigInteger prime) {
        synchronized (this) {
            primes.add(prime);
        }
    }

    public void printPrimes() {
        synchronized (this) {
            primes.forEach(System.out::println);
        }
    }
}
