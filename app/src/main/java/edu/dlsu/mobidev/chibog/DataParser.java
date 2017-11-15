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

    public HashMap<String, String> getPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();

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

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("icon", image);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        Log.i("jsonbug", jsonArray.toString());
        int count = jsonArray.length();
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap;

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

    public List<HashMap<String, String>> parse(String jsonData) {
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
