package com.yesat.takebs.support;

/**
 * Created by yesat on 10.03.2017.
 */

public class CommentClass {
    public String Comment;
    public String Rate;
    public String username;

    public CommentClass(){

    }

    public CommentClass(String comment, String rate, String username) {
        Comment = comment;
        Rate = rate;
        this.username = username;
    }
}
