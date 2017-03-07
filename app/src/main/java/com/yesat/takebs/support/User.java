package com.yesat.takebs.support;

/**
 * Created by yesat on 12.01.2017.
 */

public class User {
    public String aboutYourSelf;
    public String city;
    public String country;
    public String date;
    public String email;
    public String oneSignalId;
    public String profileImage;
    public String username;

    public User() {
    }

    public User(String aboutYourSelf, String city, String country, String date, String email, String oneSignalId, String profileImage, String username) {
        this.aboutYourSelf = aboutYourSelf;
        this.city = city;
        this.country = country;
        this.date = date;
        this.email = email;
        this.oneSignalId = oneSignalId;
        this.profileImage = profileImage;
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "aboutYourSelf='" + aboutYourSelf + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", email='" + email + '\'' +
                ", oneSignalId='" + oneSignalId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
