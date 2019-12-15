package com.Get_to_know_your_city_again;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.Get_to_know_your_city_again.models.Item;
import com.Get_to_know_your_city_again.ui.ItemRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton fab_add, fab_map, fab_comm;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_map, textview_comm;

    Boolean isOpen = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Item> itemList;
    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Item");
    private TextView noItemEntry;

    private String nameItem;
    private String item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        fab_add = findViewById(R.id.fabAdd);
        fab_map = findViewById(R.id.fabMap);
        fab_comm = findViewById(R.id.fabComm);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_map = findViewById(R.id.textview_map);
        textview_comm = findViewById(R.id.textview_comm);
        fab_add.setOnClickListener(v ->{
            if (isOpen) {

                textview_map.setVisibility(View.INVISIBLE);
                textview_comm.setVisibility(View.INVISIBLE);
                fab_comm.startAnimation(fab_close);
                fab_map.startAnimation(fab_close);
                fab_add.startAnimation(fab_anticlock);
                fab_comm.setClickable(false);
                fab_map.setClickable(false);
                isOpen = false;
            } else {
                textview_map.setVisibility(View.VISIBLE);
                textview_comm.setVisibility(View.VISIBLE);
                fab_comm.startAnimation(fab_open);
                fab_map.startAnimation(fab_open);
                fab_add.startAnimation(fab_clock);
                fab_comm.setClickable(true);
                fab_map.setClickable(true);
                isOpen = true;
            }
        });

        fab_map.setOnClickListener(v ->{
            Intent intent = new Intent(ItemListActivity.this,MapsActivity.class);
            startActivity(intent);
        });

        fab_comm.setOnClickListener(v->{
//            CommentsActivity dialogComments = new CommentsActivity();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.dialog_frame,dialogComments)
//                    .commit();
            Intent intent = new Intent(ItemListActivity.this,MapsActivity.class);
            startActivity(intent);
        });


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
