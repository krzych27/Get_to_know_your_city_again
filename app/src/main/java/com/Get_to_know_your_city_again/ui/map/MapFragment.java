package com.Get_to_know_your_city_again.ui.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.Fragment;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.PostItemActivity;
import com.Get_to_know_your_city_again.R;
import com.Get_to_know_your_city_again.model.Item;
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

    private String name_item,address_item;
    private double lat,lng,searched_lat,searched_lng;
    private Bundle mArgs;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View rl = inflater.inflate(R.layout.fragment_map, container, false);
        if(getActivity() != null)
            context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setHasOptionsMenu(true);

        map = rl.findViewById(R.id.mapView);

        itemList = new ArrayList<>();

        mArgs = getArguments();

        if(mArgs != null && !mArgs.isEmpty()){
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

            Toast.makeText(context,"This is your location",Toast.LENGTH_LONG).show();
        });


//        loadItems();

        setupMap();

       return rl;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_search);
        if(item!=null)
            item.setVisible(true);
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
                            String address_item = item.getStreet() + " " + item.getCity();
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

        if(mArgs != null && !mArgs.isEmpty() && searched_lat!=0 && searched_lng!=0){
            GeoPoint geo_point_searched = new GeoPoint(searched_lat, searched_lng);
            map.getController().setCenter(geo_point_searched);
            map.getController().animateTo(geo_point_searched);

            map.invalidate();
        }

        if(mArgs != null && !mArgs.isEmpty() && lat!=50.061219 && lat!=0 && lng!=19.936804 && lng!=0) {


            Log.d(TAG,"name from postactivity" + name_item);
            Log.d(TAG,"address from postactivity" + address_item);
            Log.d(TAG,"lat from postactivity" + String.valueOf(lat));
            Log.d(TAG,"lng from postactivity" + String.valueOf(lng));

//            addMarker(name_item, description_item, lat, lng);
            GeoPoint geoPointAdded = new GeoPoint(lat, lng);
            createMarker(name_item,address_item,geoPointAdded);

            map.getController().setCenter(geoPointAdded);
            map.getController().animateTo(geoPointAdded);
        }




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
            String address_item = item.getStreet() + " " + item.getCity();
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


    public void createMarker(String name,String address,GeoPoint geoPoint)
    {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

//        GeoPoint geoPoint = new GeoPoint(lat,lng);

        OverlayItem item = new OverlayItem(name,address,geoPoint);
        items.add(item);
        ItemizedOverlayWithFocus<OverlayItem> itemItemizedOverlayWithFocus = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(context,""+item.getTitle()+"\n"+item.getSnippet(),Toast.LENGTH_LONG).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        ItemDialogFragment itemDialogFragment = new ItemDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("name", name);
                        itemDialogFragment.setArguments(args);
                        itemDialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
                        return true;
                    }
                }, context);


        itemItemizedOverlayWithFocus.setFocusItemsOnTap(true);
        map.getOverlays().add(itemItemizedOverlayWithFocus);
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