package com.Get_to_know_your_city_again.ui.home;

import com.Get_to_know_your_city_again.models.Item;
import com.google.firebase.firestore.GeoPoint;

public interface IMapActivity {

    void createNewItem(String name, String address, String description, String type);
    void updateItem(Item item);
    void deleteItem(Item item);
    void selectItem(Item item);
}
