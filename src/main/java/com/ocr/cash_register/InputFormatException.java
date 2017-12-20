package com.ocr.cash_register;

public class InputFormatException extends Exception {

    public InputFormatException(String msg, NumberFormatException e) {
        super(msg, e);
    }

    public InputFormatException(String msg) {
        super(msg);
    }
}
