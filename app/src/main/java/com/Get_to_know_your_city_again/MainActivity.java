package com.Get_to_know_your_city_again;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.Get_to_know_your_city_again.ui.registerAndLogin.LoginActivity;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_CODE = 777 ;
    List<AuthUI.IdpConfig> providers;
    Button buttonSignOut;
    Button buttonMaps;
    Button getStartedButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db =  FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getStartedButton = findViewById(R.id.startButton);
//        setupFirebaseAuth();
//        buttonSignOut = findViewById(R.id.buttonSign_out);
//        buttonSignOut.setOnClickListener(this);
//        buttonMaps =findViewById(R.id.buttonMap);
//        buttonMaps.setOnClickListener(this);


//        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser = firebaseAuth.getCurrentUser();
                final String currentUserId = currentUser.getUid();

                collectionReference
                        .whereEqualTo("user_id", currentUserId)
                        .addSnapshotListener((EventListener<QuerySnapshot>) (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                return;
                            }

                            String name;

                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    UserApi userApi = UserApi.getInstance();
                                    userApi.setUserId(snapshot.getString("userId"));
                                    userApi.setUsername(snapshot.getString("username"));

                                    startActivity(new Intent(MainActivity.this,
                                            MapsActivity.class));
                                    finish();


                                }
                            }

                        });

            } else {

            }
        };


        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: started.");
//
//        authStateListener = firebaseAuth -> {
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//            if (user != null) {
//
//                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//
//            } else {
//                Log.d(TAG, "onAuthStateChanged:signed_out");
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//            }
//        };
//    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


}
