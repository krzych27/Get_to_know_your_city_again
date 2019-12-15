package com.Get_to_know_your_city_again;

import android.content.Intent;
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
import android.widget.Toast;


public class MapsActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MapsActivity";

    private String name_item,description_item;
    private double lat,lng;
    private Bundle mArgs = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map, R.id.nav_user_list_items, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        name_item = getIntent().getStringExtra("name");
        description_item = getIntent().getStringExtra("description");
        if(name_item !=null && description_item!=null) {
            lng = getIntent().getDoubleExtra("lng", 50.0);
            lat = getIntent().getDoubleExtra("lat", 50.0);
        }
        mArgs.putDouble("lat",lat);
        mArgs.putDouble("lng",lng);
        mArgs.putString("name",name_item);
        mArgs.putString("description",description_item);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(mArgs);
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMap,mapFragment)
                .addToBackStack(null)
                .commit();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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


}
