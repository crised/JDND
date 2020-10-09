package com.example.demo;

public class AppException extends RuntimeException {

    public AppException() {
        super("oops App Exception");
    }

    public AppException(String message) {
        super(message);
    }
}
