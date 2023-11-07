package org.example.core.java.multithreaded;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedBigPrimes {
    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();
        PrimeResults results = new PrimeResults();
        Runnable task = new PrimeGenerator(results);
        Runnable currentStatus = new CurrentStatus(results);
        Thread statusTask = new Thread(currentStatus);
        statusTask.start();

        List<Thread> threads = new ArrayList<>();
        for(int i=0; i < 100; i++) {
            Thread t = new Thread(task);
            threads.add(t);
            t.start();
        }

        for(Thread t: threads) t.join();

        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + " ms.");
        System.out.println("primes size: " + results.getSize());
    }
}
