package com.Get_to_know_your_city_again.ui.favouritesUserListItems;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.models.Items;
import com.Get_to_know_your_city_again.models.Users;
import com.Get_to_know_your_city_again.ui.comments.CommentsActivity;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ItemRecyclerAdapter";
    private Context context;
    private List<Items> itemsList;

    private List<Users> usersList;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference favouritesItemsCollectionReference;
    private DocumentReference documentReference = db.collection("Users").document();

//    private Locale locale = context.getResources().getConfiguration().locale;

    public ItemRecyclerAdapter(Context context, List<Items> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }


    @NonNull
    @Override
    public ItemRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_row,viewGroup,false);

        return new ViewHolder(view,context);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerAdapter.ViewHolder viewHolder, int position) {

        Items items = itemsList.get(position);
        String imageUrl;

        viewHolder.item_id = items.getItem_id();
        viewHolder.item_name = items.getName();
        viewHolder.item_address = items.getStreet() + " " + items.getCity();
        viewHolder.item_lat = items.getGeoPoint().getLatitude();
        viewHolder.item_lng = items.getGeoPoint().getLongitude();

        String address = "Address:\n " + items.getStreet() + " " + items.getCity();
        String description = "Description: " + items.getDescription();
        String type = "Type:\n " + items.getType();
        Log.d(TAG,"item_id "+ viewHolder.item_id);
        viewHolder.name.setText(items.getName());
        viewHolder.address.setText(address);
//        viewHolder.description.setText(items.getDescription());
        viewHolder.description.setText(description);
        viewHolder.type.setText(type);
        viewHolder.addedBy.setText(items.getUsername());
        imageUrl = items.getImageUrl();

        Log.d(TAG,"imageUrl\t"+ imageUrl);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.add_image)
                .fit()
                .into(viewHolder.image);

        Date dateCreated = items.getTimestamp();
        String date = DateFormat.getDateTimeInstance().format(dateCreated);

        viewHolder.dateAdded.setText(date);


    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,address,description,addedBy,type,dateAdded;
        public ImageView image;

        String item_id,item_name,item_address;
        Double item_lat,item_lng;

        public ImageButton mapButton;
        public ImageButton commentButton;
        public ImageButton favouriteButton;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.item_name_list);
            address = itemView.findViewById(R.id.item_address);
            description = itemView.findViewById(R.id.item_description_list);
            addedBy = itemView.findViewById(R.id.item_username_list);
            type = itemView.findViewById(R.id.item_type_list);
            dateAdded = itemView.findViewById(R.id.item_timestamp_list);
            image = itemView.findViewById(R.id.item_image_list);

            favouriteButton = itemView.findViewById(R.id.item_row_favourite_button);
            mapButton = itemView.findViewById(R.id.item_row_map_button);
            commentButton = itemView.findViewById(R.id.item_row_comment_button);

            Log.d(TAG,"item_id in ViewHolder "+ item_id);

            favouriteButton.setOnClickListener(v->{

                Map<String,String> favouritesUserItems = new HashMap<>();
                favouritesUserItems.put("item_id",item_id);

                favouritesItemsCollectionReference = collectionReference
                        .document(UserApi.getInstance().getUserId())
                        .collection("FavouritesItems");

                favouritesItemsCollectionReference
                        .add(favouritesUserItems)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG,"Succesfully added to favourites items");
                            Toast.makeText(context,"Added to favourites items",Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

            });

            mapButton.setOnClickListener(v->{
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("lat",item_lat);
                intent.putExtra("lng",item_lng);
                intent.putExtra("name",item_name);
                intent.putExtra("address",item_address);
                Log.d(TAG,"item_name in mapButton "+ item_name);
                Log.d(TAG,"item_address in mapButton "+ item_address);
                Log.d(TAG,"item_lat in mapButton "+ item_lat);
                Log.d(TAG,"item_lng in mapButton "+ item_lng);
                context.startActivity(intent);
            });

            commentButton.setOnClickListener(v->{
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("item_id",item_id);
                Log.d(TAG,"item_id in commentButton "+ item_id);
                context.startActivity(intent);
            });
        }
    }
}
