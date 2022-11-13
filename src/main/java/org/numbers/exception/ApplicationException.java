package org.numbers.exception;

public class ApplicationException extends RuntimeException{

    public ApplicationException(String message, Exception originalException) {
        super(message, originalException);
    }
}
