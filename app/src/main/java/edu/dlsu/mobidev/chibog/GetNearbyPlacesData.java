package edu.dlsu.mobidev.chibog;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JasonDeniega on 17/10/2017.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GoogleMap gMap;
    String url;
    ArrayList<Place> places;
    @Override
    protected String doInBackground(Object... objects) {
        gMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        places = (ArrayList<Place>) objects[2];

        DownloadURL downloadURL = new DownloadURL();
        try {
            //read all url lines and returns a JSON object
            googlePlacesData = downloadURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList;
        Log.i("Data",s);
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);

    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            // TODO figure out how to place an image from a URL. There's a ImageView na sa RecyclerView

            Place place = new Place(placeName, vicinity,
                                    googlePlace.get("icon"), lat, lng);
            places.add(place);
            gMap.addMarker(markerOptions);
            gMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

}
