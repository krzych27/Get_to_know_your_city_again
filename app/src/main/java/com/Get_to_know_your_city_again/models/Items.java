package com.Get_to_know_your_city_again.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Items {

    private String name;
    private String street;
    private String city;
    private String description;
    private GeoPoint geoPoint;
    private String imageUrl;
    private String type;
    private @ServerTimestamp Date timestamp;
//    private String comments_id;
    private  @DocumentId String item_id;
    private String user_id;
    private String username;
//    private int likes_count; // should be add

    public Items(String name, String description, String street, String city, GeoPoint geoPoint, String imageUrl,
                 String type, Date timestamp, String item_id, String user_id, String username) {
        this.name = name;
        this.description = description;
        this.street = street;
        this.city=city;
        this.geoPoint = geoPoint;
        this.imageUrl = imageUrl;
        this.type = type;
        this.timestamp = timestamp;
//        this.comments_id = comments_id;
        this.item_id = item_id;
        this.user_id = user_id;
        this.username = username;
//        this.likes_count=likes_count;
    }

    public Items() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

//    public void setComments_id(String comments_id) {
//        this.comments_id = comments_id;
//    }
//
//    public String getComments_id() {
//        return comments_id;
//    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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
