package com.Get_to_know_your_city_again.ui;

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

import com.Get_to_know_your_city_again.CommentsActivity;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Item;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ItemRecyclerAdapter";
    private Context context;
    private List<Item> itemList;

//    private Locale locale = context.getResources().getConfiguration().locale;

    public ItemRecyclerAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
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

        Item item = itemList.get(position);
        String imageUrl;

        viewHolder.item_id = item.getItem_id();

        Log.d(TAG,"item_id "+ viewHolder.item_id);
        viewHolder.name.setText(item.getName());
        viewHolder.address.setText(item.getAddress());
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
        public TextView name,address,description,addedBy,type,dateAdded;

        public ImageView image;
        String userId;
        String username;
        String item_id;

        public ImageButton mapButton;
        public ImageButton commentButton;


        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.item_name_list);
            address = itemView.findViewById(R.id.item_address_list);
            description = itemView.findViewById(R.id.item_description_list);
            addedBy = itemView.findViewById(R.id.item_username_list);
            type = itemView.findViewById(R.id.item_type_list);
            dateAdded = itemView.findViewById(R.id.item_timestamp_list);
            image = itemView.findViewById(R.id.item_image_list);

            mapButton = itemView.findViewById(R.id.item_row_map_button);
            commentButton = itemView.findViewById(R.id.item_row_comment_button);

            Log.d(TAG,"item_id in ViewHolder "+ item_id);

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
