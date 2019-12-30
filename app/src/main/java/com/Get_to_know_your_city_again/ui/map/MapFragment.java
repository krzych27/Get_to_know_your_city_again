package com.Get_to_know_your_city_again.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.ui.items.ItemListActivity;
import com.Get_to_know_your_city_again.ui.items.PostItemActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.models.Items;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements LocationListener {

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
    private CollectionReference collectionReference = db.collection("Items");
    private List<Items> itemsList;
    private List<com.google.firebase.firestore.GeoPoint> geoPoints;

    private String name_item, address_item;
    private double lat, lng, searched_lat, searched_lng;
    private Bundle mArgs;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rl = inflater.inflate(R.layout.fragment_map, container, false);
        if (getActivity() != null)
            context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setHasOptionsMenu(true);

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        map = rl.findViewById(R.id.mapView);

        itemsList = new ArrayList<>();

        mArgs = getArguments();

        if (mArgs != null && !mArgs.isEmpty()) {
            name_item = mArgs.getString("name");
            address_item = mArgs.getString("address");
            lng = mArgs.getDouble("lng");
            lat = mArgs.getDouble("lat");
            searched_lng = mArgs.getDouble("searched_lng");
            searched_lat = mArgs.getDouble("searched_lat");
        }


        fab_add = rl.findViewById(R.id.fabAdd);
        fab_item = rl.findViewById(R.id.fabItem);
        fab_location = rl.findViewById(R.id.fabLocation);
        fab_close = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fab_rotate_anticlock);


        fab_add.setOnClickListener(v -> {
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

        fab_item.setOnClickListener(v -> {
            Intent intent = new Intent(context,
                    PostItemActivity.class);
//            Intent intent = new Intent(context,
//                    ItemListActivity.class);
//            intent.putExtra("name","Zazdrość");
            startActivity(intent);
        });

        fab_location.setOnClickListener(v -> {


//            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }

        });


        setupMap();
        setUserLocation();
        loadItems();

        return rl;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if (item != null)
            item.setVisible(true);
    }

    private void loadItems() {

        collectionReference
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                            Items item = items.toObject(Items.class);
                            itemsList.add(item);
//                            geoPoints.add(item.getGeoPoint());
                            int count = itemsList.size();
                            Log.d(TAG, "size of itemsList is " + String.valueOf(count));

                            Log.d(TAG, "loaded items to itemsList");

                            double lat = item.getGeoPoint().getLatitude();
                            double lng = item.getGeoPoint().getLongitude();
                            String name_item = item.getName();
                            String address_item = item.getStreet() + " " + item.getCity();
                            String description_item = item.getDescription();
                            String item_id = item.getItem_id();

                            Log.d(TAG, "name" + name_item);
                            Log.d(TAG, "description" + description_item);
                            Log.d(TAG, "lat" + String.valueOf(lat));
                            Log.d(TAG, "lng" + String.valueOf(lng));

                            GeoPoint geoPointAdded = new GeoPoint(lat, lng);
                            createMarker(name_item, address_item, geoPointAdded);

                        }

                    } else {
                        Log.d(TAG, "queryDocument is empty");

                    }
                })
                .addOnFailureListener(e -> {
                });
    }


    // initializing map
    private void setupMap() {

        map.post(() -> {
            map.getTileProvider().clearTileCache();
            map.getTileProvider().setTileSource(TileSourceFactory.MAPNIK);
            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
            map.getController().setZoom((double) 12);
            map.setMultiTouchControls(true);
            map.setTilesScaledToDpi(true);
            map.setMinZoomLevel((double) 7);
            map.setMaxZoomLevel((double) 20);
        });

        // create geoPoint from user location

        GeoPoint geoPoint = new GeoPoint(50.095612, 18.542085);
        map.getController().setCenter(geoPoint);
        map.getController().animateTo(geoPoint);

        map.invalidate();

//        if(mArgs != null && !mArgs.isEmpty() && searched_lat!=0 && searched_lng!=0){
//            GeoPoint geo_point_searched = new GeoPoint(searched_lat, searched_lng);
//            map.getController().setCenter(geo_point_searched);
//            map.getController().animateTo(geo_point_searched);
//
//            map.invalidate();
//        }

        if (mArgs != null && !mArgs.isEmpty() && lat != 50.061219 && lat != 0 && lng != 19.936804 && lng != 0) {


            Log.d(TAG, "name from postactivity" + name_item);
            Log.d(TAG, "address from postactivity" + address_item);
            Log.d(TAG, "lat from postactivity" + String.valueOf(lat));
            Log.d(TAG, "lng from postactivity" + String.valueOf(lng));

//            addMarker(name_item, description_item, lat, lng);
            GeoPoint geoPointAdded = new GeoPoint(lat, lng);
            createMarker(name_item, address_item, geoPointAdded);

            map.getController().setCenter(geoPointAdded);
            map.getController().animateTo(geoPointAdded);
        }


    }

    private void setUserLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        fab_location.setOnClickListener(v -> {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location == null) {
                Toast.makeText(context, "GPS signal not found", Toast.LENGTH_SHORT).show();
            }
            if (location != null) {
                Toast.makeText(context, "This is your location", Toast.LENGTH_LONG).show();
                GeoPoint center = new GeoPoint(location.getLatitude(),location.getLongitude());
                Log.d(TAG,"Location user: " + location.getLatitude() + ", " + location.getLongitude());
                map.getController().setCenter(center);
                map.getController().animateTo(center);
                map.invalidate();

                onLocationChanged(location);
            }
        });
    }


    private void animateToMarker(double searched_lat,double searched_lng){
        if(mArgs != null && !mArgs.isEmpty() && searched_lat!=0 && searched_lng!=0){
            GeoPoint geo_point_searched = new GeoPoint(searched_lat, searched_lng);
            map.getController().setCenter(geo_point_searched);
            map.getController().animateTo(geo_point_searched);

            map.invalidate();
        }
    }

    private void createMarker(String name,String address,GeoPoint geoPoint)
    {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

//        GeoPoint geoPoint = new GeoPoint(lat,lng);

        OverlayItem item = new OverlayItem(name,address,geoPoint);
        Drawable marker = context.getDrawable(R.drawable.marker_cluster);
//        item.setMarker(marker);
//        item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);

        items.add(item);
        ItemizedOverlayWithFocus<OverlayItem> itemItemizedOverlayWithFocus = new ItemizedOverlayWithFocus<OverlayItem>(items,
                marker,null,0,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(context,""+item.getTitle()+"\n"+item.getSnippet(),Toast.LENGTH_LONG).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Intent intent = new Intent(context,
                                ItemListActivity.class);
                        intent.putExtra("name",name);
                        startActivity(intent);
                        return true;
                    }
                }, context);


        itemItemizedOverlayWithFocus.setFocusItemsOnTap(true);
        itemItemizedOverlayWithFocus.setMarkerBackgroundColor(Color.MAGENTA);
        map.getOverlays().add(itemItemizedOverlayWithFocus);
        map.getController().setCenter(geoPoint);
        map.getController().animateTo(geoPoint);

        map.invalidate();
    }


    @Override
    public void onLocationChanged(Location location) {
        GeoPoint center = new GeoPoint(location.getLatitude(),location.getLongitude());
        Log.d(TAG,"Location user in onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        map.getController().setCenter(center);
        map.getController().animateTo(center);
        map.invalidate();
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
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                setUserLocation();
                break;
            default:
                break;
        }
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
        animateToMarker(searched_lat,searched_lng);
    }

    @Override
    public void onResume() {
        super.onResume();
//        animateToMarker(searched_lat,searched_lng);
    }
}