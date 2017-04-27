package com.yesat.takebs.support;

import java.io.Serializable;

/**
 * Created by yesat on 12.01.2017.
 */

public class Route2 implements Serializable {
    public Route2() {
    }

    public String from;
    public String date;
    public String clock;
    public String cost;
    public String phoneNumber;
    public String method;
    public String info;
    public String username;
    public String uid;
    public String ImageUrl;

    public Route2(String from, String date, String clock, String cost, String phoneNumber, String method, String info, String username, String uid, String imageUrl) {
        this.from = from;
        this.date = date;
        this.clock = clock;
        this.cost = cost;
        this.phoneNumber = phoneNumber;
        this.method = method;
        this.info = info;
        this.username = username;
        this.uid = uid;
        this.ImageUrl = imageUrl;
    }

    public boolean have(String filter) {
        return (from.toLowerCase().contains(filter) ||
                date.toLowerCase().contains(filter) ||
                phoneNumber.toLowerCase().contains(filter) ||
                info.toLowerCase().contains(filter) ||
                username.toLowerCase().contains(filter));
    }

    public Route toRoute() {
        String[] ft = from.split("\\s\\-\\s");
        return new Route(ft[0],ft[1],date,clock,cost,phoneNumber,method,info,null,username,uid,ImageUrl);
    }
}

