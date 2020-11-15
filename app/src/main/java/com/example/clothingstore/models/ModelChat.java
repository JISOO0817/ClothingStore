package com.example.clothingstore.models;

import com.example.clothingstore.adapters.AdapterMessage;

public class ModelChat {

    private String sender;
    private String receiver;
    private String msg;
    private String time;


    public ModelChat() {
    }

    public ModelChat(String sender, String receiver, String msg, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

