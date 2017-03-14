package com.yesat.takebs.support;

/**
 * Created by yesat on 26.02.2017.
 */

public class Chat {
    public String fromId;
    public String text;
    public Double timestamp;
    public String toId;


    public Chat() {
    }

    public Chat(String fromId, String text, Double timestamp, String toId) {
        this.fromId = fromId;
        this.text = text;
        this.timestamp = timestamp;
        this.toId = toId;
    }
}
