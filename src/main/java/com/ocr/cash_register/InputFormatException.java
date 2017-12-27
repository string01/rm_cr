package com.ocr.cash_register;

/**
 * Exception thrown when an input string is invalid.
 */
public class InputFormatException extends Exception {

    public InputFormatException(String msg, NumberFormatException e) {
        super(msg, e);
    }

    public InputFormatException(String msg) {
        super(msg);
    }
}
