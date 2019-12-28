package com.Get_to_know_your_city_again;

import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.Get_to_know_your_city_again.ui.map.MapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MapsActivity";

    private TextView textView_user_name,textView_user_email;
    private String item_name, item_address,user_name,user_email;
    private double lat,lng;
    private Bundle mArgs = new Bundle();
    private String search_string;

    private MyAsyncTask myAsyncTask = null;
    private HashMap<String, Double> cords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textView_user_name = headerView.findViewById(R.id.nav_header_userName);
        textView_user_email = headerView.findViewById(R.id.nav_header_userEmail);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map, R.id.nav_user_list_items, R.id.nav_list_favourites)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        user_name = getIntent().getStringExtra("username");
        user_email = getIntent().getStringExtra("email");
        Log.d(TAG,"user_name "+ user_name);
        Log.d(TAG,"user_email "+ user_email);
        if(user_name !=null && user_email !=null){
            textView_user_name.setText(user_name);
            textView_user_email.setText(user_email);
        }
        item_name = getIntent().getStringExtra("name");
        item_address = getIntent().getStringExtra("address");
        if(item_name !=null && item_address !=null) {
            lng = getIntent().getDoubleExtra("lng", 50.061219);
            lat = getIntent().getDoubleExtra("lat", 19.936804);
        }

        if(item_name !=null && item_address !=null && lat!=0 && lng !=0){
            mArgs.putDouble("lat",lat);
            mArgs.putDouble("lng",lng);
            mArgs.putString("name", item_name);
            mArgs.putString("address", item_address);

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(mArgs);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameMap,mapFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search city");
        searchView.setOnQueryTextListener(this);
        searchItem.setVisible(false);
        searchView.setIconified(false);

        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.maps, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();
                break;
            case R.id.action_settings:
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;
//            case R.id.action_search:
//                MapFragment mapFragment = new MapFragment();
//                Bundle searched_city = new Bundle();
//                String name_city;
//                searched_city.putString("searched_city",name_city);
//                mapFragment.setArguments(mArgs);
//                this.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameMap,mapFragment)
//                        .addToBackStack(null)
//                        .commit();
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        Toast.makeText(MapsActivity.this,"Signing out",Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MapsActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        cords = Geocode(query);
        if(cords.isEmpty()) {
            Log.d(TAG,"Hashmap is empty");
        }

        double lat = cords.get("lat");
        double lng = cords.get("lng");

        MapFragment mapFragment = new MapFragment();
        Bundle searched_city = new Bundle();
        searched_city.putDouble("searched_lat",lat);
        searched_city.putDouble("searched_lng",lng);
        mapFragment.setArguments(searched_city);
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMap,mapFragment)
                .addToBackStack(null)
                .commit();
        Toast.makeText(this, "Looking for " +query, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private HashMap<String, Double> getCoordinates(List<Address> geoResults){

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
                Toast.makeText(MapsActivity.this, "Geocoding error! Internet available?", Toast.LENGTH_SHORT).show();
            }

            return cords;
        }


        protected void onPostExecute(HashMap<String, Double> cords) {
            super.onPostExecute(cords);


            if (cords.size() == 0)  //if no address found, display an error
                Toast.makeText(MapsActivity.this, "Object not found", Toast.LENGTH_SHORT).show();


        }
    }

    public HashMap<String, Double> Geocode(String name){

        HashMap<String, Double> result = null;
        try {
            this.myAsyncTask = new MapsActivity.MyAsyncTask();
            this.myAsyncTask.execute(name);
            result=this.myAsyncTask.get();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return result;

    }
}
