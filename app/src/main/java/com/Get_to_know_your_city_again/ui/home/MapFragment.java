package com.Get_to_know_your_city_again.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.MainActivity;
import com.Get_to_know_your_city_again.MapsActivity;
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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements LocationListener  {

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
    double lat;
    double lng;
//    ArrayList<OverlayItem> items = null;
//    private GeocoderNominatim geocoderNominatim;
//    private Geocode geocode;

    private final String userAgent = BuildConfig.APPLICATION_ID;


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


       return rl;

    }


    // initializing map
    private void setupMap() {


//        map.post(() -> {
//            map.getTileProvider().setTileSource(TileSourceFactory.MAPNIK);
//            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
//            map.getController().setZoom(12.);
//            map.setMultiTouchControls(true);
//            map.setMinZoomLevel(7.);
//            map.setMaxZoomLevel(15.);
//        });

        map.getTileProvider().clearTileCache();
        map.getTileProvider().setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);

        map.setMinZoomLevel((double)7);
        map.setMaxZoomLevel((double)20);

        GeoPoint startPoint = new GeoPoint(50.0954, 18.5419);

        mapController = map.getController();

        mapController.setZoom((double)12);


        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        Geocode("Kopalnia Ignacy",items);

        int size = items.size();
        Log.d("size arraylist of items",String.valueOf(size));

        Log.d("lat in CreateMarker",String.valueOf(lat));
        Log.d("lng in CreateMarker",String.valueOf(lng));

//        addToArrayItem(items,"Kopalnia Ignacy","Zabytkowa kopalnia węgla kamiennego w Rybniku",lat,lng);
//        CreateMarker();

//        point = Geocode("Mikołowska 4 Rybnik");
//        double lat3 = point.get(0);
//        double lng3 = point.get(1);
//        Log.d("lat3",String.valueOf(lat3));
//        Log.d("lat3",String.valueOf(lng3));
//        GeoPoint object3 = new GeoPoint(lat3,lng3);
//        addMarker(object3,"Bazylika św. Antoniego w Rybniku");
//        point.clear();
//        map.invalidate();

//        CreateMarker();
//        mapController.setCenter(startPoint);
//
//        mapController.animateTo(startPoint);
//        addMarker(startPoint);





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

    private void addToArrayItem( ArrayList<OverlayItem> items,String name,String description,double lat,
            double lng ) {
        OverlayItem item = new OverlayItem(name,description,new GeoPoint(lat,lng));
        items.add(item);
    }

    private void CreateMarker()
    {
//        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();


//        addToArrayItem(items,"Kopalnia Ignacy","Zabytkowa kopalnia węgla kamiennego w Rybniku",lat,lng);

//        OverlayItem item1 = new OverlayItem("Rynek w Rybniku","Rybnicki Rynek to nie tylko główny, ale zarazem najbardziej reprezentacyjny plac miasta. Wytyczono go podczas lokacji miasta na przełomie XIII i XIV wieku, ale nie znajdziemy zabytków z tego okresu. Głównie dlatego, że aż do końca XVIII stulecia rynek otoczony był drewnianą zabudową. Zmiany nastąpiły po 1788 r., czyli wtedy, kiedy miasto znalazło się pod rządami Państwa Pruskiego.\n" +
//                "\n" +
//                "Obecnie rynek otoczony jest uroczymi kamieniczkami, które reprezentują kilka różnych stylów. Dominuje klasycyzm, eklektyzm, ale znajdziemy też budynki neorenesansowe oraz wczesnomodernistyczne.\n" +
//                "\n" +
//                "Warto zwrócić uwagę na Dawny Ratusz (1823 r.), gdzie obecnie mieści się muzeum a także Urząd Stanu Cywilnego. Na płycie rynku znajdziemy też fontannę oraz wieńczącą ją figurę św. Jana Nepomucena. Wokół placu ustawiono drewniane ławeczki, świetnym miejscem do letniego relaksu są również letnie ogródki restauracyjne.\n" +
//                "\n"
//                , new GeoPoint(50.0954, 18.5419));
//
//        OverlayItem item2 = new OverlayItem("Zalew Rybnicki", "Zbiornik zaporowy utworzony przez spiętrzenie wód rzecznych Rudy zaporą w Rybniku Stodołach.\n"+
//                "Zbiornik o powierzchni 4,5 km² i objętości 22 mln m³ wody został utworzony na terenie dzielnic Rybnika – Rybnickiej Kuźni, Orzepowic, Chwałęcic i Stodół, dla potrzeb Elektrowni Rybnik.\n"
//                , new GeoPoint(50.135833, 18.502222));
//
//        items.add(item1);
//        items.add(item2);

//        MyItemizedOverlay myItemizedOverlay = new MyItemizedOverlay(context,items);
//
//        myItemizedOverlay.setFocusItemsOnTap(true);
//        map.getOverlays().add(myItemizedOverlay);
//
//        map.invalidate();
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

    private class MyAsyncTask extends AsyncTask<String,Integer, List<Address>> {

//        MapView pMap;
//        Context pContext;
//        double lat;
//        double lng;
        Locale pCurrent = getResources().getConfiguration().locale;
        private final String userAgent = BuildConfig.APPLICATION_ID;
        GeocoderNominatim geocoderNominatim = new GeocoderNominatim(pCurrent,userAgent);
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        ProgressDialog progressDialog;
        String countryTitleString;

        public MyAsyncTask(ArrayList<OverlayItem> items) {
            this.items = items;
        }

        //        private MyAsyncTask(Context context, Locale current, MapView map) {
//            this.pContext = context;
//            this.pCurrent = current;
//            this.pMap = map;
//        }

//        private MyAsyncTask(MapView map) {
//            this.pMap = map;
//        }

//        protected List<Address> doInBackground(String... countryTitle)
        protected List<Address> doInBackground(String... params) {

//            countryTitleString = Arrays.toString(countryTitle);

            List<Address> geoResults = null;
            try {
                geoResults = geocoderNominatim.getFromLocationName(params[0], 1);
                Log.d("result",String.valueOf(geocoderNominatim));
            } catch (IOException e) {
                e.printStackTrace();
//                Toast.makeText(pContext, "Geocoding error! Internet available?", Toast.LENGTH_SHORT).show();
            }
            return geoResults;

        }


        protected void onPostExecute(List<Address> geoResults) {
            super.onPostExecute(geoResults);


            if (geoResults.size() == 0) { //if no address found, display an error
//                Toast.makeText(pContext, countryTitleString +" - Country not found.", Toast.LENGTH_SHORT).show();
            } else {
                Address address = geoResults.get(0);
                Bundle extras = address.getExtras();
                Log.d("extras",String.valueOf(extras));
                BoundingBox bb = extras.getParcelable("boundingbox");
                Log.d("boundingbox",String.valueOf(bb));
                lat = bb.getLatNorth();
                lng = bb.getLonEast();
                Log.d("lat",String.valueOf(lat));
                Log.d("lng",String.valueOf(lng));

                addToArrayItem(items,"Kopalnia Ignacy","Zabytkowa kopalnia węgla kamiennego w Rybniku",lat,lng);

                GeoPoint object = new GeoPoint(lat,lng);
                mapController.setCenter(object);
                mapController.animateTo(object);

                MyItemizedOverlay myItemizedOverlay = new MyItemizedOverlay(context,items);

                myItemizedOverlay.setFocusItemsOnTap(true);
                map.getOverlays().add(myItemizedOverlay);

                map.invalidate();


//                CreateMarker();
//                addMarker(object,"Zabytkowa kopalnia węgla kamiennego w Rybniku");
//                CreateMarker();
//
//                map.invalidate();

                //should be return array[lat,lng]

//                Toast.makeText(pContext, countryTitleString, Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void Geocode(String name, ArrayList<OverlayItem> items){
        this.myAsyncTask = new MyAsyncTask(items);
        this.myAsyncTask.execute(name);

    }



}