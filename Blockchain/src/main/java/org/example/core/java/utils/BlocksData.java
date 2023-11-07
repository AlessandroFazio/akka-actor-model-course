package org.example.core.java.utils;

import org.example.core.java.model.Block;
import org.example.core.java.model.Transaction;

import java.util.GregorianCalendar;

public class BlocksData {
    private static long[] timestamps = { new GregorianCalendar(2015, 5, 22, 14, 21).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 27).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 28).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 30).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 40).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 45).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 47).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 48).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 52).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 53).getTimeInMillis(),
            new GregorianCalendar(2015, 5, 22, 14, 59).getTimeInMillis(),
    };

    private static int[] customerIds = {1732, 1650, 2209, 3304, 1499, 4559, 3432, 4300, 2300, 5522, 7611};
    private static double[] amounts = {103.27, 66.54, -21.09, 44.65, 177.99, 189.02, 17.00, 32.99, 99.60, -10.00, 100.44};

    public static Block getNextBlock(int id, String lastHash) {
        Transaction transaction = new Transaction(id, timestamps[id], customerIds[id], amounts[id]);
        return new Block(lastHash, transaction);
    }
}
