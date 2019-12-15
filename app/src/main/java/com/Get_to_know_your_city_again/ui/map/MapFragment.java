package com.Get_to_know_your_city_again.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.ItemListActivity;
import com.Get_to_know_your_city_again.MapsActivity;
import com.Get_to_know_your_city_again.PostItemActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Item;
import com.Get_to_know_your_city_again.ui.ItemRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements LocationListener{

    private final String TAG = "MapFragment";
    private MapView map;
    private Context context;
    private FloatingActionButton fab_add, fab_item, fab_location;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private boolean isOpen = true;

    private GpsMyLocationProvider gpsMyLocationProvider;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationNewOverlay;

    private LocationManager locationManager;
    private Locale current;

    private ArrayList<Double> coordinates = new ArrayList<>();

    private HashMap<String, Double> cords;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Item");
    private List<Item> itemList;
    private List<com.google.firebase.firestore.GeoPoint> geoPoints;

    private String name_item,description_item;
    private double lat,lng;
    private Bundle mArgs;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View rl = inflater.inflate(R.layout.fragment_map, container, false);
        if(getActivity() != null)
            context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = rl.findViewById(R.id.mapView);

        itemList = new ArrayList<>();

        mArgs = getArguments();

        if(mArgs != null && !mArgs.isEmpty()){
            name_item = mArgs.getString("name");
            description_item = mArgs.getString("description");
            lng = mArgs.getDouble("lng");
            lat = mArgs.getDouble("lat");
        }


        fab_add = rl.findViewById(R.id.fabAdd);
        fab_item = rl.findViewById(R.id.fabItem);
        fab_location = rl.findViewById(R.id.fabLocation);
        fab_close = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_rotate_anticlock);


        fab_add.setOnClickListener(v ->{
            if (isOpen) {

//                textview_map.setVisibility(View.INVISIBLE);
//                textview_comm.setVisibility(View.INVISIBLE);
                fab_location.startAnimation(fab_close);
                fab_item.startAnimation(fab_close);
                fab_add.startAnimation(fab_anticlock);
                fab_location.setClickable(false);
                fab_item.setClickable(false);
                isOpen = false;
            } else {
//                textview_map.setVisibility(View.VISIBLE);
//                textview_comm.setVisibility(View.VISIBLE);
                fab_location.startAnimation(fab_open);
                fab_item.startAnimation(fab_open);
                fab_add.startAnimation(fab_clock);
                fab_location.setClickable(true);
                fab_item.setClickable(true);
                isOpen = true;
            }
        });

        fab_item.setOnClickListener(v ->{
            Intent intent = new Intent(context,
                    PostItemActivity.class);
//            Intent intent = new Intent(context,
//                    ItemListActivity.class);
//            intent.putExtra("name","Zazdrość");
            startActivity(intent);
        });

        fab_location.setOnClickListener(v->{
//            CommentsActivity dialogComments = new CommentsActivity();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.dialog_frame,dialogComments)
//                    .commit();
            Toast.makeText(context,"This is your location",Toast.LENGTH_LONG).show();
        });


//        loadItems();

        setupMap();

       return rl;

    }

    private void loadItems(){

        collectionReference
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            Item item = items.toObject(Item.class);
                            itemList.add(item);
//                            geoPoints.add(item.getGeoPoint());
                            int count = itemList.size();
                            Log.d(TAG,"size of itemList is " + String.valueOf(count));

                            Log.d(TAG,"loaded items to itemList");

                            double lat = item.getGeoPoint().getLatitude();
                            double lng = item.getGeoPoint().getLongitude();
                            String name_item = item.getName();
                            String address_item = item.getAddress();
                            String description_item = item.getDescription();
                            String item_id = item.getItem_id();

                            Log.d(TAG,"name" + name_item);
                            Log.d(TAG,"description" + description_item);
                            Log.d(TAG,"lat" + String.valueOf(lat));
                            Log.d(TAG,"lng" + String.valueOf(lng));


                            addMarker(name_item,description_item,lat,lng);

                        }

                    }else{
                            Log.d(TAG,"queryDocument is empty");

                    }
                })
                .addOnFailureListener(e ->{} );
    }




    // initializing map
    private void setupMap() {

        map.post(() -> {
            map.getTileProvider().clearTileCache();
            map.getTileProvider().setTileSource(TileSourceFactory.MAPNIK);
            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
            map.getController().setZoom((double)12);
            map.setMultiTouchControls(true);
            map.setTilesScaledToDpi(true);
            map.setMinZoomLevel((double)7);
            map.setMaxZoomLevel((double)20);
        });

        // create geoPoint from user location

        GeoPoint geoPoint = new GeoPoint(50.095612, 18.542085);
        map.getController().setCenter(geoPoint);
        map.getController().animateTo(geoPoint);

        map.invalidate();

        if(mArgs != null && !mArgs.isEmpty() && lat!=50.0 && lng!=50.0) {


            Log.d(TAG,"name from postactivity" + name_item);
            Log.d(TAG,"description from postactivity" + description_item);
            Log.d(TAG,"lat from postactivity" + String.valueOf(lat));
            Log.d(TAG,"lng from postactivity" + String.valueOf(lng));

            addMarker(name_item, description_item, lat, lng);
            GeoPoint geoPointAdded = new GeoPoint(lat, lng);
            map.getController().setCenter(geoPointAdded);
            map.getController().animateTo(geoPointAdded);
        }

//        if(isLoaded)
//            loadingMarkes();

//        String name = "Rynek Główny w Krakowie";
//        String address = "Rynek Główny 1 Kraków";
//        String userId = "7rt4lXZTgMViXJtW91Bh";
//        double lat1 = 50.061783;
//        double lng1 = 19.9375356;
//
//        String name2 = "Zamek królewski na Wawelu";
//        String address2 = "Wawel 5 Kraków";
//        String userId2 = "ebzeroy9jeOGs7XmNUxI";
//        double lat2 = 50.0550047;
//        double lng2 = 19.9356345;
//
//        createMarker(name,address,userId,lat1,lng1);
//        createMarker(name2,address2,userId2,lat2,lng2);


//        createMarker(cords);
        //add to database with name,address,geopoint,description, imageurl,type, user_id



//        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


//        gpsMyLocationProvider = new GpsMyLocationProvider(context);
//        myLocationNewOverlay =  new MyLocationNewOverlay(gpsMyLocationProvider,map);
//        myLocationNewOverlay.enableMyLocation();
//        myLocationNewOverlay.enableFollowLocation();
//
//        icon = BitmapFactory.decodeResource(getResources(),R.drawable.marker_cluster);
////        myLocationNewOverlay.setPersonIcon(icon);
////        map.getOverlays().add(myLocationNewOverlay);
//        myLocationNewOverlay.runOnFirstFix( () -> {
//            map.getOverlays().clear();
//            map.getOverlays().add(myLocationNewOverlay);
//            map.invalidate();
//            mapController.animateTo(myLocationNewOverlay.getMyLocation());
//
//        });



//        map.post(new Runnable() {
//            @Override
//            public void run() {
//                map.getController().setZoom(12.);
//                map.getController().animateTo(new GeoPoint(45., 2));
//            }
//        });

    }

    private void loadingMarkes (){

        Log.d(TAG,"loadingMarkes method");
        int count = itemList.size();
        Log.d(TAG," loadingMarkes: size of itemList is " + String.valueOf(count));

        for(Item item : itemList){
            double lat = item.getGeoPoint().getLatitude();
            double lng = item.getGeoPoint().getLongitude();
            String name_item = item.getName();
            String address_item = item.getAddress();
            String description_item = item.getDescription();
            String item_id = item.getItem_id();

            Log.d(TAG,"name" + name_item);
            Log.d(TAG,"description" + description_item);
            Log.d(TAG,"lat" + String.valueOf(lat));
            Log.d(TAG,"lng" + String.valueOf(lng));


            addMarker(name_item,description_item,lat,lng);

        }
    }


    private void addMarker(String name,String description,double lat,double lng){
        Marker marker = new Marker(map);
        GeoPoint geoPoint = new GeoPoint(lat,lng);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.osm_ic_center_map));
        marker.setTitle(name);
        marker.setSnippet(description);
        marker.setOnMarkerClickListener((marker1, mapView) ->{

//            String name_item = marker1.getTitle();
            ItemDialogFragment itemDialogFragment = new ItemDialogFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            itemDialogFragment.setArguments(args);
            itemDialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
            return true;
        });
        map.getOverlays().clear();
        map.getOverlays().add(marker);
        map.invalidate();

    }


    public void createMarker(String name,String address,String item_id,double lat,double lng)
    {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        GeoPoint geoPoint = new GeoPoint(lat,lng);
        Log.d(TAG,"lat after async" + String.valueOf(lat));
        Log.d(TAG,"lng after async" + String.valueOf(lng));

        OverlayItem item = new OverlayItem(item_id,name,address,geoPoint);
        items.add(item);

        MyItemizedOverlay myItemizedOverlay = new MyItemizedOverlay(context,items);

        myItemizedOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(myItemizedOverlay);

        map.getController().setCenter(geoPoint);
        map.getController().animateTo(geoPoint);

        map.invalidate();
    }


    @Override
    public void onLocationChanged(Location location) {
        GeoPoint center = new GeoPoint(location.getLatitude(),location.getLongitude());
        mapController.animateTo(center);
//        addMarker(center,"My Location");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(locationManager!= null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        loadItems();

    }
}