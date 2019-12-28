package com.Get_to_know_your_city_again;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Button loginButton;
    private Button createAccountLoginButton;

    private AutoCompleteTextView emailAddres;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progress);

//        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();


        emailAddres = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.email_sign_in_button);
        createAccountLoginButton = findViewById(R.id.create_account_button_login);

        createAccountLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,
                    CreateAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v ->
                loginEmailPasswordUser(emailAddres.getText().toString().trim(),
                password.getText().toString().trim()));

    }

    private void loginEmailPasswordUser(String email, String pwd) {

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        final String currentUserId = user.getUid();

                        collectionReference
                                .whereEqualTo("user_id", currentUserId)
                                .addSnapshotListener((queryDocumentSnapshots, e) -> {

                                    if (e != null) {
                                    }
                                    assert queryDocumentSnapshots != null;
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            UserApi userApi = UserApi.getInstance();
                                            userApi.setUsername(snapshot.getString("username"));
                                            userApi.setUserId(snapshot.getString("user_id"));
                                            userApi.setEmail(snapshot.getString("email"));

                                            Log.d(TAG,"username"+snapshot.getString("username"));
                                            Log.d(TAG,"userId"+snapshot.getString("userId"));
                                            Log.d(TAG,"email"+snapshot.getString("email"));

                                            Intent intent = new Intent(LoginActivity.this,
                                                    MapsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }
                                });
                    })
                    .addOnFailureListener(e ->{
                        progressBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(LoginActivity.this, "Wrong entered data",
//                                Toast.LENGTH_LONG).show();

                    });

        }else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Please enter email and password",
                    Toast.LENGTH_LONG).show();
        }
    }


    public boolean servicesOK(){
        Log.d(TAG, "servicesOK: Checking Google Services.");

        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(isAvailable == ConnectionResult.SUCCESS){
            //everything is ok and the user can make mapping requests
            Log.d(TAG, "servicesOK: Play Services is OK");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            //an error occured, but it's resolvable
            Log.d(TAG, "servicesOK: an error occured, but it's resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Can't connect to services", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
