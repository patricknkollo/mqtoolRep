package com.mqTool.pojos;


public class QueueObject {

    private String name;
    private String messages;
    private String exchange;

    public QueueObject() {
    }

    public QueueObject(String name, String messages, String exchange) {
        this.name = name;
        this.messages = messages;
        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "QueueObject{" +
                "name='" + name + '\'' +
                ", messages='" + messages + '\'' +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
