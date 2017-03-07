package com.yesat.takebs.support;

/*
 * Created by yesat on 28.02.2017.
 */

import java.io.Serializable;

public class ChatPerson implements Serializable {
    public String uid;
    public String lastMes;

    public ChatPerson(String uid, String lastMes) {
        this.uid = uid;
        this.lastMes = lastMes;
    }
}
