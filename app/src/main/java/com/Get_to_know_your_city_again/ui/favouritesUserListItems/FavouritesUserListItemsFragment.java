package com.Get_to_know_your_city_again.ui.favouritesUserListItems;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Items;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouritesUserListItemsFragment extends Fragment {

    private static final String TAG = "FavouritesItemsFragment";
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Items> itemsList;
    private Map<String,String> favouritesUserItems = new HashMap<>();
    private ArrayList<String> favouritesItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    private CollectionReference itemCollectionReference = db.collection("Items");
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference favouritesItemsCollectionReference;
    private DocumentReference documentReference = db.collection("Users").document();


    private TextView noItemEntry;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View rl = inflater.inflate(R.layout.fragment_list_favourites, container, false);
        if(getActivity() != null)
            context = getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        setHasOptionsMenu(false);

        noItemEntry = rl.findViewById(R.id.list_favourites_text);
        itemsList = new ArrayList<>();

        recyclerView = rl.findViewById(R.id.list_favourites_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rl;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = UserApi.getInstance().getUserId();
        String name_city;
        Log.d(TAG,"userId is" + userId);

        favouritesItemsCollectionReference = collectionReference
                .document(UserApi.getInstance().getUserId())
                .collection("FavouritesItems");

        favouritesItemsCollectionReference
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            favouritesItems.add(items.getString("item_id"));
                            Log.d(TAG,"item_id in favouritesCollection " + items.getString("item_id"));
                        }
                        Log.d(TAG,"number of favourites Items after query: " + favouritesItems.size());
                        for(String items_id : favouritesItems){
                            Log.d(TAG,"current item_id in for is: " + items_id);
                            itemCollectionReference.whereEqualTo(FieldPath.documentId(),items_id)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                        if (!queryDocumentSnapshots2.isEmpty()) {
                                            for (QueryDocumentSnapshot items : queryDocumentSnapshots2) {
                                                Items item = items.toObject(Items.class);
                                                itemsList.add(item);
                                            }
                                            Log.d(TAG,"number of itemsList is: " + itemsList.size());
                                            favouritesItems.remove(items_id);
                                            if((favouritesItems.isEmpty())){
                                                itemRecyclerAdapter = new ItemRecyclerAdapter(this.context, itemsList);
                                                recyclerView.setAdapter(itemRecyclerAdapter);
                                            }
                                        }else {
                                            noItemEntry.setVisibility(View.VISIBLE);
                                            Log.d(TAG,"item queryDocument is empty");
                                        }

                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

                        }
                        Log.d(TAG,"number of itemsList after for is: " + itemsList.size());


                    }else {
                        noItemEntry.setVisibility(View.VISIBLE);
                        Log.d(TAG,"favourites queryDocument is empty");

                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

        Log.d(TAG,"number of favourites Items: " + favouritesItems.size());



//        if(!itemsList.isEmpty()){
//            itemRecyclerAdapter = new ItemRecyclerAdapter(this.context,itemsList);
//            recyclerView.setAdapter(itemRecyclerAdapter);
//        }else{
//            noItemEntry.setVisibility(View.VISIBLE);
//            Log.d(TAG,"queryDocument is empty");
//        }
    }
}