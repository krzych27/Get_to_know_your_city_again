package com.Get_to_know_your_city_again;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.Get_to_know_your_city_again.models.Item;
import com.Get_to_know_your_city_again.utils.UserApi;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.BoundingBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostItemActivity";
    private static final int GALLERY_CODE = 1;
    private Context context;

    private Button addItemButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText descriptionEditText;
    private ImageView imageView;
    private Spinner typeSpinner;


    private String currentUserId;
    private String currentUserName;
    private String typeItem;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Item");
    private Uri imageUri;

    private DocumentReference documentReference = db.collection("Item").document();

    private MyAsyncTask myAsyncTask = null;
    private HashMap<String, Double> cords;
    private GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.item_progressBar);
        nameEditText = findViewById(R.id.name_item);
        addressEditText = findViewById(R.id.address_item);
        descriptionEditText = findViewById(R.id.description_item);
        typeSpinner = findViewById(R.id.type_spinner);


        final ArrayList<String> types = new ArrayList<>();
        types.add("building");
        types.add("natural");
        types.add("industrial");
        types.add("theme park");
        types.add("monuments");

        ArrayAdapter<String> adapterTypes = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(adapterTypes);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"You selected"+
                        types.get(position),Toast.LENGTH_SHORT).show();
                typeItem = types.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.d(TAG,"Selected type is" + typeItem);

        addItemButton = findViewById(R.id.add_item_button);
        cancelButton = findViewById(R.id.cancel_button);
        imageView = findViewById(R.id.item_CameraButton);

        addItemButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        imageView.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.add_item_button:{
                SaveItem();

                break;
            }

            case R.id.cancel_button:{
                Intent intent = new Intent(PostItemActivity.this,
                        MapsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.item_CameraButton:{
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
            }
        }
    }

    private void SaveItem(){

        final String name = nameEditText.getText().toString().trim();
        final String address = addressEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String type = typeItem;

        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG,"name"+name);
        Log.d(TAG,"address"+address);
        Log.d(TAG,"description"+description);
        Log.d(TAG,"type"+type);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(description) && !TextUtils.isEmpty(type)
                && imageUri != null) {

            cords = Geocode(name);
            if(cords.isEmpty()) {
                Log.d(TAG,"Hashmap is empty");
            }

            double lat = cords.get("lat");
            double lng = cords.get("lng");

            geoPoint = new GeoPoint(lat,lng);

            final StorageReference filepath = storageReference
                    .child("item_images")
                    .child("object_image_" + Timestamp.now().getSeconds());
            filepath.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                        String imageUrl = uri.toString();

                        Item item = new Item();
                        item.setName(name);
                        item.setAddress(address);
                        item.setDescription(description);
                        item.setType(type);
                        item.setItem_id(documentReference.getId());
                        item.setImageUrl(imageUrl);

                        // upload coordinates from geocoding
                        item.setGeoPoint(geoPoint);

                        item.setUsername(currentUserName);
                        item.setUser_id(currentUserId);


                        collectionReference.add(item)
                                .addOnSuccessListener(documentReference -> {

                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostItemActivity.this,
                                            ItemListActivity.class));
                                })
                                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

                    }))
                    .addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));


        } else {

            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private  HashMap<String, Double> getCoordinates( List<Address> geoResults){

        Address address = geoResults.get(0);
        Bundle extras = address.getExtras();
        Log.d("extras",String.valueOf(extras));
        BoundingBox bb = extras.getParcelable("boundingbox");
        Log.d("boundingbox",String.valueOf(bb));
        double latitude = bb.getLatNorth();
        double longitude = bb.getLonEast();

        Log.d("lat after async",String.valueOf(latitude));
        Log.d("lng after async",String.valueOf(longitude));

        HashMap<String, Double> cords = new HashMap<>();
        cords.put("lat",latitude);
        cords.put("lng",longitude);

        return cords;

    }

    public class MyAsyncTask extends AsyncTask<String,Integer, HashMap<String, Double> > {

        PostItemActivity postItemActivity;
        Locale pCurrent = getResources().getConfiguration().locale;
        private final String userAgent = BuildConfig.APPLICATION_ID;
        GeocoderNominatim geocoderNominatim = new GeocoderNominatim(pCurrent,userAgent);


//        public MyAsyncTask(PostItemActivity postItemActivity) {
//            this.postItemActivity=postItemActivity;
//        }

        protected HashMap<String, Double> doInBackground(String... params) {

            List<Address> geoResults = null;
            try {
                geoResults = geocoderNominatim.getFromLocationName(params[0], 1);
                cords = getCoordinates(geoResults);
                Log.d("result",String.valueOf(geocoderNominatim));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Geocoding error! Internet available?", Toast.LENGTH_SHORT).show();
            }

            return cords;
        }


        protected void onPostExecute(HashMap<String, Double> cords) {
            super.onPostExecute(cords);


            if (cords.size() == 0)  //if no address found, display an error
                Toast.makeText(context, "Object not found", Toast.LENGTH_SHORT).show();


        }
    }

    public HashMap<String, Double> Geocode(String name){

        HashMap<String, Double> result = null;
        try {
            this.myAsyncTask = new MyAsyncTask();
            this.myAsyncTask.execute(name);
            result=this.myAsyncTask.get();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image
                imageView.setImageURI(imageUri);//show image

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
