package edu.dlsu.mobidev.chibog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity {

    protected GeoDataApi mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
