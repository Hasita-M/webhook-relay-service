package com.codewithhasita.webrelayretryservice;

public class ReceiverNotFoundException extends RuntimeException {
    public ReceiverNotFoundException(String message) {
        super(message);
    }
}
