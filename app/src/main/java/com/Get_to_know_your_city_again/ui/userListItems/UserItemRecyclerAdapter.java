package com.Get_to_know_your_city_again.ui.userListItems;

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

import com.Get_to_know_your_city_again.EditItemActivity;
import com.Get_to_know_your_city_again.ui.comments.CommentsActivity;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.model.Item;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class UserItemRecyclerAdapter extends RecyclerView.Adapter<UserItemRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ItemRecyclerAdapter";
    private Context context;
    private List<Item> itemList;

    public UserItemRecyclerAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
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

        Item item = itemList.get(position);
        String imageUrl;

        viewHolder.item_id = item.getItem_id();

        Log.d(TAG,"item_id "+ viewHolder.item_id);
        viewHolder.name.setText(item.getName());
        viewHolder.street.setText(item.getStreet());
        viewHolder.city.setText(item.getCity());
        viewHolder.description.setText(item.getDescription());
        viewHolder.type.setText(item.getType());
        viewHolder.addedBy.setText(item.getUsername());
        imageUrl = item.getImageUrl();

        Log.d(TAG,"imageUrl\t"+ imageUrl);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.add_image)
                .fit()
                .into(viewHolder.image);

//        String pattern = "HH:mm:ss dd/MM/yyyy";
//        DateFormat df = new SimpleDateFormat(pattern,locale);
//        DateFormat df = new SimpleDateFormat(pattern);

        Date dateCreated = item.getTimestamp();
        String date = DateFormat.getDateInstance().format(dateCreated);

        viewHolder.dateAdded.setText(date);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,street,city,description,addedBy,type,dateAdded;

        public ImageView image;
        String userId;
        String username;
        String item_id;

        public ImageButton mapButton;
        public ImageButton commentButton;
        public ImageButton editButton;


        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.item_user_row_name_list);
            street = itemView.findViewById(R.id.item_user_row_street_list);
            city = itemView.findViewById(R.id.item_user_row_city_list);
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
