package com.yesat.takebs.support;

import java.io.Serializable;

/**
 * Created by yesat on 12.01.2017.
 */

public class Route implements Serializable {
    public Route() {
    }

    public String fromCity;
    public String fromCountry;
    public String toCity;
    public String toCountry;
    public String date;
    public String time;
    public String shippingCost;
    public String contactNumber;
    public String deliveryMethod;
    public String note;
    public String username;
    public String uid;
    public String url;

    public Route(String fromCity, String fromCountry, String toCity, String toCountry, String date, String time, String shippingCost, String contactNumber, String deliveryMethod, String note, String username, String uid, String url) {
        this.fromCity = fromCity;
        this.fromCountry = fromCountry;
        this.toCity = toCity;
        this.toCountry = toCountry;
        this.date = date;
        this.time = time;
        this.shippingCost = shippingCost;
        this.contactNumber = contactNumber;
        this.deliveryMethod = deliveryMethod;
        this.note = note;
        this.username = username;
        this.uid = uid;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Route{" +
                "fromCity='" + fromCity + '\'' +
                ", fromCountry='" + fromCountry + '\'' +
                ", toCity='" + toCity + '\'' +
                ", toCountry='" + toCountry + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", shippingCost='" + shippingCost + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", deliveryMethod='" + deliveryMethod + '\'' +
                ", note='" + note + '\'' +
                ", username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

