package com.Get_to_know_your_city_again.ui.items;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Items;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserListItemsFragment extends Fragment {

    private static final String TAG = "UserListItemsFragment";
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Items> itemsList;
    private RecyclerView recyclerView;
    private UserItemRecyclerAdapter userItemRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Items");
    private TextView noItemEntry;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        galleryViewModel =
//                ViewModelProviders.of(this).get(GalleryViewModel.class);


        View rl = inflater.inflate(R.layout.fragment_user_list_item, container, false);
        if(getActivity() != null)
            context = getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        setHasOptionsMenu(false);

        noItemEntry = rl.findViewById(R.id.no_list_items);
        itemsList = new ArrayList<>();

        recyclerView = rl.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rl;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = UserApi.getInstance().getUserId();
        Log.d(TAG,"userId is" + userId);

        collectionReference.whereEqualTo("user_id", UserApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            Items item = items.toObject(Items.class);
                            itemsList.add(item);
                        }

                        userItemRecyclerAdapter = new UserItemRecyclerAdapter(this.context, itemsList);
                        recyclerView.setAdapter(userItemRecyclerAdapter);

                    }else {
                        noItemEntry.setVisibility(View.VISIBLE);
                        Log.d(TAG,"queryDocument is empty");

                    }

                })
                .addOnFailureListener(e -> {

                });

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        collectionReference.whereEqualTo("userId", UserApi.getInstance()
//                .getUserId())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
//                            Items item = items.toObject(Items.class);
//                            itemsList.add(item);
//                        }
//
////                        itemRecyclerAdapter = new ItemRecyclerAdapter(this.context,itemsList);
////                        recyclerView.setAdapter(itemRecyclerAdapter);
//                        itemRecyclerAdapter.updateWith(itemsList);
//
//                    }else {
//                        noItemEntry.setVisibility(View.VISIBLE);
//
//                    }
//
//                })
//                .addOnFailureListener(e -> {
//
//                });
//
//    }
}