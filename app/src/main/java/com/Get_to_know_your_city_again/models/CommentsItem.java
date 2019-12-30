package com.Get_to_know_your_city_again.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentsItem {

    private String item_id;
    private String comment_text;
    private String user_name;
    private @ServerTimestamp Date timecreated;
    private  @DocumentId String comment_id;

    public CommentsItem(String item_id, String comment_text, String user_name, Date timecreated,String comment_id) {
        this.item_id = item_id;
        this.comment_text = comment_text;
        this.user_name = user_name;
        this.timecreated = timecreated;
        this.comment_id =comment_id;
    }

    public CommentsItem() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public Date getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Date timecreated) {
        this.timecreated = timecreated;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
