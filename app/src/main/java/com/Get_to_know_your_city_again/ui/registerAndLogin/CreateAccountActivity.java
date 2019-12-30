package com.Get_to_know_your_city_again.ui.registerAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Users;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    private DocumentReference documentReference = db.collection("Users").document();

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountButton;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if(currentUser !=null){
                // user is already login
            }else{
                // no yet login
            }
        };

        createAccountButton.setOnClickListener(v->{
            if(!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = userNameEditText.getText().toString().trim();

                if( password.length() < 8){
                    Toast.makeText(CreateAccountActivity.this,"Passwords must be at least 8 characters",Toast.LENGTH_LONG).show();
                }else{
                    createUserEmailAccount(email,password,username);

                    Log.d(TAG,"username in createAccountButton"+username);
                    Log.d(TAG,"email in createAccountButton"+ email);
                }
            }else{
                Toast.makeText(CreateAccountActivity.this,"Empty fields not allowed",Toast.LENGTH_LONG).show();
            }


        });
    }

    private void createUserEmailAccount(String email,String password,String username){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            //take user to map activity
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            Users users = new Users();
                            users.setUser_id(currentUserId);
                            users.setUsername(username);
                            users.setEmail(email);
//                            Map<String,String> userObject = new HashMap<>();
//                            userObject.put("userId",currentUserId);
//                            userObject.put("username",username);

                            collectionReference.document(currentUserId).set(users)
                                    .addOnSuccessListener(v->{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        UserApi userApi = UserApi.getInstance();
                                        userApi.setUserId(currentUserId);
                                        userApi.setUsername(username);
                                        userApi.setEmail(email);

                                        Log.d(TAG,"username"+username);
                                        Log.d(TAG,"userId"+ currentUserId);
                                        Log.d(TAG,"email"+email);

                                        Intent intent = new Intent(CreateAccountActivity.this,
                                                MapsActivity.class);

                                        intent.putExtra("username", username);
                                        intent.putExtra("userId", currentUserId);
                                        intent.putExtra("email",email);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e-> Log.d(TAG, "onFailure: " + e.getMessage()));


//                            collectionReference.add(users)
//                                    .addOnSuccessListener(documentReference -> documentReference.get()
//                                            .addOnCompleteListener(task1 -> {
//                                                if(Objects.requireNonNull(task1.getResult()).exists()){
//                                                    progressBar.setVisibility(View.INVISIBLE);
//                                                    String name = task1.getResult()
//                                                            .getString("username");
//
//                                                    UserApi userApi = UserApi.getInstance();
//                                                    userApi.setUserId(currentUserId);
//                                                    userApi.setUsername(name);
//                                                    userApi.setEmail(email);
//
//                                                    Log.d(TAG,"username"+name);
//                                                    Log.d(TAG,"userId"+ currentUserId);
//                                                    Log.d(TAG,"email"+email);
//
//                                                    Intent intent = new Intent(CreateAccountActivity.this,
//                                                            MapsActivity.class);
//
////                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                                    intent.putExtra("username", name);
//                                                    intent.putExtra("userId", currentUserId);
//                                                    intent.putExtra("email",email);
//                                                    startActivity(intent);
//
//                                                }else{
//                                                    progressBar.setVisibility(View.INVISIBLE);
//                                                }
//                                            }))
//                                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
//
//                        }else{
//
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}
