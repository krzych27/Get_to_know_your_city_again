package com.Get_to_know_your_city_again;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.Get_to_know_your_city_again.models.Item;
import com.Get_to_know_your_city_again.ui.ItemRecyclerAdapter;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Item> itemList;
    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Item");
    private TextView noItemEntry;

    private String nameItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noItemEntry = findViewById(R.id.no_list_items2);
        itemList = new ArrayList<>();

        nameItem = getIntent().getStringExtra("name");

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"nameItem is " + nameItem);




        collectionReference.whereEqualTo("name", nameItem)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            Item item = items.toObject(Item.class);
                            itemList.add(item);
                        }
                        Log.d(TAG,"before setAdapter");
                        itemRecyclerAdapter = new ItemRecyclerAdapter(ItemListActivity.this,
                                itemList);
                        recyclerView.setAdapter(itemRecyclerAdapter);
                        itemRecyclerAdapter.notifyDataSetChanged();

                    }else {
                        noItemEntry.setVisibility(View.VISIBLE);
                        Log.d(TAG,"queryDocument is empty");

                    }

                })
                .addOnFailureListener(e -> {

                });



    }
}
