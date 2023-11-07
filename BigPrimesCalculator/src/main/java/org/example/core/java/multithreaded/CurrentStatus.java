package org.example.core.java.multithreaded;

public class CurrentStatus implements Runnable {
    private PrimeResults results;

    public CurrentStatus(PrimeResults results) {
        this.results = results;
    }

    @Override
    public void run() {
        while (results.getSize() < 100) {
            System.out.println(results.getSize());
            results.printPrimes();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
