package com.example.clothingstore.models;

public class ModelMessage {

    private String sender;
    private String receiver;
    private String msg;
    private String time;
    private String messageId;


    public ModelMessage() {
    }

    public ModelMessage(String sender, String receiver, String msg, String time, String messageId) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.time = time;
        this.messageId = messageId;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}

