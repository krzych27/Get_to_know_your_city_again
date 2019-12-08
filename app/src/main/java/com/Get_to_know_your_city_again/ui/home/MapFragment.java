package com.Get_to_know_your_city_again.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements LocationListener{

    private MapView map;
    private Context context;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationNewOverlay;
    private Bitmap icon;
    private LocationManager locationManager;
    private Locale current;
    private MyAsyncTask myAsyncTask = null;
    private String name = "Kopalnia Ignacy";
    private double lat;
    private double lng;
    private ArrayList<Double> coordinates = new ArrayList<>();

    private HashMap<String, Double> cords;

    private final String userAgent = BuildConfig.APPLICATION_ID;
    private final String TAG = "MapFragment Error";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View rl = inflater.inflate(R.layout.fragment_map, container, false);
        if(getActivity() != null)
            context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = rl.findViewById(R.id.mapView);

        FloatingActionButton fab = rl.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            NewItemDialog dialog = new NewItemDialog();
            dialog.show( getActivity().getSupportFragmentManager(), getString(R.string.dialog_new_item));
        });


//        current = getResources().getConfiguration().locale;

       setupMap();

//       double longitude = coordinates.get(1);
//       double latitude = coordinates.get(0);
//        Log.d("lat after async",String.valueOf(latitude));
//        Log.d("lng after async",String.valueOf(longitude));


       return rl;

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

        cords = Geocode("Kopalnia Ignacy");

        createMarker(cords);
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

    private void addMarker(GeoPoint center,String name){
        Marker marker = new Marker(map);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.osm_ic_center_map));
        marker.setTitle(name);
        marker.setImage(getResources().getDrawable(R.drawable.fui_ic_twitter_bird_white_24dp));

        map.getOverlays().clear();
        map.getOverlays().add(marker);
        map.invalidate();

    }

    private void addToArrayItem( ArrayList<OverlayItem> items,String name,String address,double lat,
            double lng ) {
        OverlayItem item = new OverlayItem(name,address,new GeoPoint(lat,lng));
        items.add(item);
    }

    private void createMarker(HashMap<String, Double> cords)
    {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        if(cords.isEmpty()) {
            Log.d(TAG,"Hashmap is empty");
        }

        double lat = cords.get("lat");
        double lng = cords.get("lng");
        GeoPoint geoPoint = new GeoPoint(lat,lng);
        Log.d("lat after async",String.valueOf(lat));
        Log.d("lng after async",String.valueOf(lng));

        addToArrayItem(items,"Kopalnia Ignacy","Zabytkowa kopalnia węgla kamiennego w Rybniku",lat,lng);

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
        addMarker(center,"My Location");
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



    private class MyAsyncTask extends AsyncTask<String,Integer, HashMap<String, Double> >{

        MapFragment mapFragment;
        Locale pCurrent = getResources().getConfiguration().locale;
        private final String userAgent = BuildConfig.APPLICATION_ID;
        GeocoderNominatim geocoderNominatim = new GeocoderNominatim(pCurrent,userAgent);

        public MyAsyncTask(MapFragment mapFragment) {
            this.mapFragment = mapFragment;
        }

        protected HashMap<String, Double> doInBackground(String... params) {

            List<Address> geoResults = null;
            try {
                geoResults = geocoderNominatim.getFromLocationName(params[0], 1);
                cords = mapFragment.getCoordinates(geoResults);
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

    private HashMap<String, Double> Geocode(String name){

        HashMap<String, Double> result = null;
        try {
            this.myAsyncTask = new MyAsyncTask(this);
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