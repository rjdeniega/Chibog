package edu.dlsu.mobidev.chibog;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    public Place getPlace(JSONObject googlePlaceJSON) {
        Place googlePlaceMap = new Place();

        String placeName = "-NA-";
        String vicinity = "-NA-";

        //extract data from JSON and add it to hashmap
        try {
            if (!googlePlaceJSON.isNull("name"))
                placeName = googlePlaceJSON.getString("name");

            if (!googlePlaceJSON.isNull("vicinity"))
                vicinity = googlePlaceJSON.getString("vicinity");

            String latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            String reference = googlePlaceJSON.getString("reference");
            String image = googlePlaceJSON.getString("icon");

            googlePlaceMap.setName(placeName);
            googlePlaceMap.setVicinity(vicinity);
            googlePlaceMap.setLat(Double.parseDouble(latitude));
            googlePlaceMap.setLng(Double.parseDouble(longitude));
            googlePlaceMap.setReference(reference);
            googlePlaceMap.setImageUrl(image);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private ArrayList<Place> getPlaces(JSONArray jsonArray) {
        Log.i("jsonbug", jsonArray.toString());
        int count = jsonArray.length();
        ArrayList<Place> placelist = new ArrayList<>();
        Place placeMap;

        for (int i = 0; i < count; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placelist;
    }

    public ArrayList<Place> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        Log.d("jsondata", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}
