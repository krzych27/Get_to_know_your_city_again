package com.Get_to_know_your_city_again.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentItem {

    private String user_id;
    private String item_id;
    private String comment_id;
    private String text;
    private @ServerTimestamp Date timestamp;

    public CommentItem(String user_id, String item_id, String comment_id, String text, Date timestamp) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.comment_id = comment_id;
        this.text = text;
        this.timestamp = timestamp;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
