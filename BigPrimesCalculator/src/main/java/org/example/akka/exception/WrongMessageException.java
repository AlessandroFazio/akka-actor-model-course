package org.example.akka.exception;

public class WrongMessageException extends RuntimeException {
    public WrongMessageException(String message) {
        super(message);
    }
}
