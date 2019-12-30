package com.Get_to_know_your_city_again.ui.comments;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.CommentsItem;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG  = "CommentsActivity";
    private ImageButton addCommentButton;
    private EditText commentInputText;
    private TextView commentText;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private String currentUserId;
    private String currentUserName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<CommentsItem> commentsItems;
    private String item_id;
    private String comment_id;



    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CollectionReference collectionReference = db.collection("Items");
    private CollectionReference commentCollectionReference;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_comments);

        addCommentButton = findViewById(R.id.add_comment_button);
        commentInputText = findViewById(R.id.comment_input_text);
        commentText = findViewById(R.id.comment_text);
        progressBar = findViewById(R.id.comment_add_progressBar);
        mSwipeRefreshLayout = findViewById(R.id.comment_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (UserApi.getInstance() != null) {
            currentUserId = UserApi.getInstance().getUserId();
            currentUserName = UserApi.getInstance().getUsername();

        }

        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {

            } else {

            }
        };

        progressBar.setVisibility(View.INVISIBLE);

        commentsItems = new ArrayList<>();

        item_id = getIntent().getStringExtra("item_id");
        Log.d(TAG,"item_id is  "+ item_id);

        commentCollectionReference = collectionReference.document(item_id).collection("Comments");


        addCommentButton.setOnClickListener(v->SaveComment());

        recyclerView = findViewById(R.id.comment_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    protected void SaveComment(){

        final String commmentText = commentInputText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(commmentText)) {

            Log.d(TAG,"comment text is "+commmentText);

            CommentsItem commentsItem = new CommentsItem();
            commentsItem.setComment_text(commmentText);
            commentsItem.setItem_id(item_id);
            commentsItem.setUser_name(currentUserName);

            commentCollectionReference
                    .add(commentsItem)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.INVISIBLE);

                    })
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    protected void getComments(){

        commentCollectionReference.whereEqualTo("item_id", item_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            CommentsItem comment = items.toObject(CommentsItem.class);
                            commentsItems.add(comment);
                        }
                        Log.d(TAG,"before setAdapter in UpdateComment");
                        commentsRecyclerAdapter = new CommentsRecyclerAdapter(CommentsActivity.this,
                                commentsItems);
                        recyclerView.setAdapter(commentsRecyclerAdapter);
                        commentsRecyclerAdapter.notifyDataSetChanged();

                    }else {

                        Log.d(TAG,"queryDocument is empty UpdateComment");

                    }

                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG," onStart() item_id is " + item_id);

        getComments();

    }

    @Override
    public void onRefresh() {
        getComments();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
