package com.Get_to_know_your_city_again.ui.items;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Items;
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

public class EditItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditItemActivity";
    private static final int GALLERY_CODE = 1;

    private Button editItemButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private EditText nameEditText;
    private EditText streetEditText;
    private EditText cityEditText;
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

    private CollectionReference collectionReference = db.collection("Items");
    private DocumentReference documentReference = db.collection("Items").document();
    private Uri imageUri;



    private MyAsyncTask myAsyncTask = null;
    private HashMap<String, Double> cords;
    private GeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);


        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.item_progressBar);
        nameEditText = findViewById(R.id.name_item);
        streetEditText = findViewById(R.id.street_item);
        cityEditText = findViewById(R.id.city_item);
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
                Toast.makeText(getApplicationContext(),"You should choice type of item",Toast.LENGTH_LONG).show();
            }
        });

        Log.d(TAG,"Selected type is" + typeItem);

        editItemButton = findViewById(R.id.edit_item_button);
        cancelButton = findViewById(R.id.cancel_button);
        imageView = findViewById(R.id.item_CameraButton);

        editItemButton.setOnClickListener(this);
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

            case R.id.edit_item_button:{
                editItem();

                break;
            }

            case R.id.cancel_button:{
                Intent intent = new Intent(EditItemActivity.this,
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

    private void editItem(){

        final String name = nameEditText.getText().toString().trim();
        final String street = streetEditText.getText().toString().trim();
        final String city = cityEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String type = typeItem;

        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG,"name"+name);
        Log.d(TAG,"street"+street);
        Log.d(TAG,"city"+city);
        Log.d(TAG,"description"+description);
        Log.d(TAG,"type"+type);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(street) &&
                !TextUtils.isEmpty(description) && !TextUtils.isEmpty(type)
                && !TextUtils.isEmpty(city) &&imageUri != null) {

            cords = Geocode(street+" "+city);
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

                        Items items = new Items();
                        items.setName(name);
                        items.setStreet(street);
                        items.setCity(city);
                        items.setDescription(description);
                        items.setType(type);
                        items.setImageUrl(imageUrl);

                        String address = street + " " + city;

                        // upload coordinates from geocoding
                        items.setGeoPoint(geoPoint);

                        items.setUsername(currentUserName);
                        items.setUser_id(currentUserId);

                        collectionReference.add(items)
                                .addOnSuccessListener(documentReference -> {

                                    progressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(EditItemActivity.this,
                                            MapsActivity.class);

                                    intent.putExtra("lat",lat);
                                    intent.putExtra("lng",lng);
                                    intent.putExtra("name",name);
                                    intent.putExtra("address",address);
//                                    intent.putExtra("description",description);
                                    startActivity(intent);

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
                Toast.makeText(EditItemActivity.this, "Geocoding error! Internet available?", Toast.LENGTH_SHORT).show();
            }

            return cords;
        }


        protected void onPostExecute(HashMap<String, Double> cords) {
            super.onPostExecute(cords);


            if (cords.size() == 0)  //if no address found, display an error
                Toast.makeText(EditItemActivity.this, "Object not found", Toast.LENGTH_SHORT).show();


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

