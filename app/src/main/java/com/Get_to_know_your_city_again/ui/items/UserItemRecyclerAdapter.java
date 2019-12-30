package com.Get_to_know_your_city_again.ui.items;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.models.Items;
import com.Get_to_know_your_city_again.ui.comments.CommentsActivity;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class UserItemRecyclerAdapter extends RecyclerView.Adapter<UserItemRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ItemRecyclerAdapter";
    private Context context;
    private List<Items> itemsList;

    public UserItemRecyclerAdapter(Context context, List<Items> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user_row,viewGroup,false);

        return new ViewHolder(view,context);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Items items = itemsList.get(position);
        String imageUrl;

        viewHolder.item_id = items.getItem_id();
        viewHolder.item_address = items.getStreet() + " " + items.getCity();
        viewHolder.item_lat = items.getGeoPoint().getLatitude();
        viewHolder.item_lng = items.getGeoPoint().getLongitude();

        String address = "Address:\n" + items.getStreet() + " " + items.getCity();
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
        public ImageButton editButton;


        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.item_user_row_name_list);
            address = itemView.findViewById(R.id.item_user_row_address);
            description = itemView.findViewById(R.id.item_user_row_description_list);
            addedBy = itemView.findViewById(R.id.item_user_row_username_list);
            type = itemView.findViewById(R.id.item_user_row_type_list);
            dateAdded = itemView.findViewById(R.id.item_user_row_timestamp_list);
            image = itemView.findViewById(R.id.item_user_row_image_list);

            editButton = itemView.findViewById(R.id.item_user_row_edit_button);
            mapButton = itemView.findViewById(R.id.item_user_row_map_button);
            commentButton = itemView.findViewById(R.id.item_user_row_comment_button);

            Log.d(TAG,"item_id in ViewHolder "+ item_id);

            editButton.setOnClickListener(v->{
                Intent intent = new Intent(context, EditItemActivity.class);
                context.startActivity(intent);
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
