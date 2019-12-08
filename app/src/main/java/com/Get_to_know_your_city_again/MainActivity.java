package com.Get_to_know_your_city_again;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.Get_to_know_your_city_again.ui.home.MapViewModel;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                        .whereEqualTo("userId", currentUserId)
                        .addSnapshotListener((EventListener<QuerySnapshot>) (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                return;
                            }

                            String name;

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

            }else {

            }
        };


        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

//        providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build()
//        );
//
//        showSignInOptions();
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//
//            case R.id.startButton:{
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
////                startActivity(new Intent(MainActivity.this,
////                        LoginActivity.class));
//                startActivity(intent);
//                finish();
//            }
//            case R.id.buttonMap:{
//                Intent intent = new Intent(this, MapsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//            case R.id.buttonSign_out:{
////                AuthUI.getInstance()
////                        .signOut(MainActivity.this)
////                        .addOnCompleteListener(task -> {
////                            buttonSignOut.setEnabled(false);
////                            buttonMaps.setEnabled(false);
////                            showSignInOptions();
////                        });
//            }
//        }
//    }


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

//    @Override
//    public void onFailure(@NonNull Exception e) {
//            Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
//    }

//    private void showSignInOptions(){
//        startActivityForResult(
//                AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setTheme(R.style.MyTheme)
//                .build(),MY_REQUEST_CODE
//        );
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if(requestCode == MY_REQUEST_CODE)
//        {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//            if(resultCode == RESULT_OK)
//            {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                Toast.makeText(this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
//                buttonSignOut.setEnabled(true);
//                buttonMaps.setEnabled(true);
//                getStartedButton.setEnabled(true);
//
//            }
//            else {
//                Toast.makeText(this,""+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
