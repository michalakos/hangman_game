package com.example.hangman;

/**
 * class to use for creating custom exceptions
 */
public abstract class BaseException extends Exception {

    private final String message;

    /**
     * create instance containing custom message
     * @param msg error message
     */
    public BaseException(String msg) {
        this.message = msg;
    }

    /**
     * retrieve message from instance
     * @return message contained in instance
     */
    public String getMessage() {
        return message;
    }
}
