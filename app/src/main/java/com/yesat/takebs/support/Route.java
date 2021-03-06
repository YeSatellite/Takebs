package com.yesat.takebs.support;

import java.io.Serializable;

/**
 * Created by yesat on 12.01.2017.
 */

public class Route implements Serializable {
    public Route() {
    }

    public String fromCity;
    public String toCity;
    public String date;
    public String time;
    public String shippingCost;
    public String contactNumber;
    public String deliveryMethod;
    public String note;
    public String selectMethod;
    public String username;
    public String uid;
    public String url;

    public Route(String fromCity, String toCity, String date, String time, String shippingCost, String contactNumber, String deliveryMethod, String note, String selectMethod, String username, String uid, String url) {
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.date = date;
        this.time = time;
        this.shippingCost = shippingCost;
        this.contactNumber = contactNumber;
        this.deliveryMethod = deliveryMethod;
        this.note = note;
        this.selectMethod = selectMethod;
        this.username = username;
        this.uid = uid;
        this.url = url;
    }

    public boolean have(String filter) {
        return (fromCity.toLowerCase().contains(filter) ||
                toCity.toLowerCase().contains(filter) ||
                date.toLowerCase().contains(filter) ||
                contactNumber.toLowerCase().contains(filter) ||
                note.toLowerCase().contains(filter) ||
                username.toLowerCase().contains(filter));
    }

    public Route2 toRoute2() {
        return new Route2(fromCity+" - "+toCity,date,time,shippingCost,contactNumber,deliveryMethod,note,username,uid,url);
    }
}

