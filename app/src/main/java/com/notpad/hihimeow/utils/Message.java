package com.notpad.hihimeow.utils;

public class Message {
    private  String message;
    private boolean messOfCurrMeow;

    public Message(String message, boolean messOfCurrMeow) {
        this.message = message;
        this.messOfCurrMeow = messOfCurrMeow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessOfCurrMeow() {
        return messOfCurrMeow;
    }

    public void setMessOfCurrMeow(boolean messOfCurrMeow) {
        this.messOfCurrMeow = messOfCurrMeow;
    }
}
