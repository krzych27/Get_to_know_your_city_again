package com.Get_to_know_your_city_again.utils;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.Get_to_know_your_city_again.BuildConfig;
import com.Get_to_know_your_city_again.ui.items.PostItemActivity;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GeocodingAsyncTask extends AsyncTask<String,Integer, HashMap<String, Double>> {

    Context context;

    private HashMap<String, Double> cords;
    private Locale loc = Locale.getDefault();
//    private Locale pCurrent = context.getResources().getConfiguration().locale;
    private final String userAgent = BuildConfig.APPLICATION_ID;
    private GeocoderNominatim geocoderNominatim = new GeocoderNominatim(loc, userAgent);


    public GeocodingAsyncTask(Context context) {
        this.context = context;
    }

    protected HashMap<String, Double> doInBackground(String... params) {

        List<Address> geoResults = null;
        try {
            geoResults = geocoderNominatim.getFromLocationName(params[0], 1);
            cords = getCoordinates(geoResults);
            Log.d("result", String.valueOf(geocoderNominatim));

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


    private HashMap<String, Double> getCoordinates(List<Address> geoResults) {

        Address address = geoResults.get(0);
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
//        Bundle extras = address.getExtras();
//        Log.d("extras",String.valueOf(extras));
//        BoundingBox bb = extras.getParcelable("boundingbox");
//        Log.d("boundingbox",String.valueOf(bb));
//        double latitude = bb.getLatNorth();
//        double longitude = bb.getLonEast();

        Log.d("lat after async", String.valueOf(latitude));
        Log.d("lng after async", String.valueOf(longitude));

        HashMap<String, Double> cords = new HashMap<>();
        cords.put("lat", latitude);
        cords.put("lng", longitude);

        return cords;

    }
}