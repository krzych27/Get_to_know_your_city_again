package com.Get_to_know_your_city_again.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Item {

    private String name;
    private String address;
    private String description;
    private GeoPoint geoPoint;
    private String imageUrl;
    private @ServerTimestamp Date timestamp;
    private String comments_id;
    private String item_id;
    private String user_id;

    public Item(String name, String description, String address,GeoPoint geoPoint, String imageUrl, Date timestamp, String comments_id, String item_id, String user_id) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.geoPoint = geoPoint;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.comments_id = comments_id;
        this.item_id = item_id;
        this.user_id = user_id;
    }

    public Item() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDecription() {
        return description;
    }

    public void setDecription(String decription) {
        this.description = decription;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setComments_id(String comments_id) {
        this.comments_id = comments_id;
    }

    public String getComments_id() {
        return comments_id;
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

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
