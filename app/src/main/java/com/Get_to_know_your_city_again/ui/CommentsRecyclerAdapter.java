package com.Get_to_know_your_city_again.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.CommentsItem;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolderComments> {


    private static final String TAG = "CommentsRecyclerAdapter";
    private Context context;
    private List<CommentsItem> commentsItemList;

    public CommentsRecyclerAdapter(Context context, List<CommentsItem> commentsItemList) {
        this.context = context;
        this.commentsItemList = commentsItemList;
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolderComments onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.comment_row,viewGroup,false);

        return new ViewHolderComments(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerAdapter.ViewHolderComments viewHolder, int position) {
        CommentsItem commentsItem = commentsItemList.get(position);

        viewHolder.text.setText(commentsItem.getComment_text());
        viewHolder.addedBy.setText(commentsItem.getUser_name());

//        String pattern = "HH:mm:ss dd/MM/yyyy";
        Date dateCreated = commentsItem.getTimecreated();
        String date = DateFormat.getDateInstance().format(dateCreated);
        viewHolder.dateAdded.setText(date);

    }

    @Override
    public int getItemCount() {
        return commentsItemList.size();
    }

    class ViewHolderComments extends RecyclerView.ViewHolder {
        TextView text,addedBy,dateAdded;

        ViewHolderComments(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            text = itemView.findViewById(R.id.comment_text_view);
            addedBy = itemView.findViewById(R.id.comment_username_view);
            dateAdded = itemView.findViewById(R.id.comment_timestamp_view);

        }
    }
}
