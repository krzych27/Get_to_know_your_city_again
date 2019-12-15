package com.Get_to_know_your_city_again.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.ItemListActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Item;
import com.Get_to_know_your_city_again.ui.ItemRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemDialogFragment extends DialogFragment {

    private static final String TAG = "ItemDialogFragment";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Item> itemList;
    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Item");
    private TextView noItemEntry;

    private String name_item;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_item_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noItemEntry = view.findViewById(R.id.no_list_items2);
        itemList = new ArrayList<>();

//        nameItem = getIntent().getStringExtra("name");

        Bundle mArgs = getArguments();
        name_item = mArgs.getString("name");

        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG,"nameItem is " + name_item);


        collectionReference.whereEqualTo("name", name_item)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            Item item = items.toObject(Item.class);
                            itemList.add(item);
                        }
                        Log.d(TAG,"before setAdapter");
                        itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(),
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
