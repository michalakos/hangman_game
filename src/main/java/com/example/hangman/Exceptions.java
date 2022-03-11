package com.example.hangman;

/**
 * custom exceptions for dictionary correctness
 */
public class Exceptions {

    /**
     * duplicate word in dictionary
     */
    public static class InvalidCountException extends BaseException {
        public InvalidCountException(String msg) {
            super(msg);
        }
    }

    /**
     * dictionary contains less than 20 words
     */
    public static class UndersizeException extends BaseException {
        public UndersizeException(String msg) {
            super(msg);
        }
    }

    /**
     * dictionary contains word with less than six words
     */
    public static class InvalidRangeException extends BaseException {
        public InvalidRangeException(String msg) {
            super(msg);
        }
    }

    /**
     * dictionary doesn't consist of at least 20% long words (nine of more letters)
     */
    public static class UnbalancedException extends BaseException {
        public UnbalancedException(String msg) {
            super(msg);
        }
    }
}