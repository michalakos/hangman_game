package com.example.hangman;

public class Exceptions {

    public static class InvalidCountException extends BaseException {
        public InvalidCountException(String msg) {
            super(msg);
        }
    }

    public static class UndersizeException extends BaseException {
        public UndersizeException(String msg) {
            super(msg);
        }
    }

    public static class InvalidRangeException extends BaseException {
        public InvalidRangeException(String msg) {
            super(msg);
        }
    }

    public static class UnbalancedException extends BaseException {
        public UnbalancedException(String msg) {
            super(msg);
        }
    }
}