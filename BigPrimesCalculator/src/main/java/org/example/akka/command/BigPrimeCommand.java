package org.example.akka.command;

import java.math.BigInteger;

public class BigPrimeCommand implements ManagerCommand {
    private static final long serialVersionUID = 1L;
    private BigInteger message;

    public BigInteger getMessage() {
        return message;
    }

    public BigPrimeCommand(BigInteger message) {
        this.message = message;
    }
}
