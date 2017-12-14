package edu.dlsu.mobidev.chibog;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static edu.dlsu.mobidev.chibog.R.id.map;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    PlaceAdapter pa;
    FavouriteAdapter favouriteAdapter;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    RelativeLayout hiddenPanel, mainScreen, hiddenPanelFavourites;
    LinearLayout pullUp, favouritesPullUp;
    ImageButton closeListOfPlaces, closeListOfFavourites, addToFavourites;
    RecyclerView rvPlaces, rvFavourites;
    ArrayList<edu.dlsu.mobidev.chibog.Place> places;
    TextView noPlaces, noFavourites;
    DatabaseHelper dbHelper;

    View get_place;
    int PROXIMITY_RADIUS = 500;
    double latitude, longitude;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // This adds a map to the layout
        if (isServicesOK()) initializeMap();
        requestPermission();

        //set-up transparent status bar\
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        get_place = findViewById(R.id.get_place);

        dbHelper = new DatabaseHelper(getBaseContext());
        noPlaces = (TextView) findViewById(R.id.no_places);
        noFavourites = (TextView)findViewById(R.id.no_places_favourites);
        hiddenPanel = (RelativeLayout) findViewById(R.id.hidden_panel);
        hiddenPanelFavourites = (RelativeLayout) findViewById(R.id.hidden_favourite);
        addToFavourites = (ImageButton) findViewById(R.id.add_favourite);
        mainScreen = (RelativeLayout) findViewById(R.id.main_screen);
        rvPlaces = (RecyclerView) findViewById(R.id.rv_places);
        rvFavourites = (RecyclerView) findViewById(R.id.rv_favourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        favouriteAdapter = new FavouriteAdapter(this, dbHelper.getAllFavourites());
        rvFavourites.setAdapter(favouriteAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(rvFavourites);

        if(dbHelper.getAllFavourites().getCount() > 0){
            noFavourites.setVisibility(View.GONE);
        }
        Log.i("fav", dbHelper.getAllFavourites().getCount() + "");

        places = new ArrayList<>();


        favouriteAdapter.setOnItemClickListener(new FavouriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long id) {
                ArrayList<Place> newList =  dbHelper.getRestaurantsFromFavourite(id);
                mGoogleMap.clear();
                for (Place p: newList){
                    MarkerOptions markerOptions = new MarkerOptions();

                    String placeName = p.getName();
                    String vicinity = p.getVicinity();
                    double lat = p.getLat();
                    double lng = p.getLng();

                    LatLng latLng = new LatLng( lat, lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : "+ vicinity);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
                    // TODO figure out how to place an image from a URL. There's a ImageView na sa RecyclerView
                    mGoogleMap.addMarker(markerOptions);
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(2));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                closeFavourites();
                addToFavourites.setVisibility(View.GONE);
                rvPlaces.setVisibility(View.VISIBLE);
                noPlaces.setVisibility(View.GONE);
                pa = new PlaceAdapter(newList);
                pa.notifyDataSetChanged();
                pa.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(edu.dlsu.mobidev.chibog.Place p) {
                        Toast.makeText(getBaseContext(), "User clicked on " + p.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                rvPlaces.setAdapter(pa);
                rvPlaces.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                        LinearLayoutManager.VERTICAL, false));
                Toast.makeText(getBaseContext(), id + " was loaded", Toast.LENGTH_SHORT).show();
            }
        });

        // Testing get nearby restaurants
        get_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                places.clear();
                Object dataTransfer[] = new Object[3];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                mGoogleMap.clear();
                String url = getUrl(latitude, longitude, "restaurant");
                dataTransfer[0] = mGoogleMap;
                dataTransfer[1] = url;
                dataTransfer[2] = places;

                getNearbyPlacesData.execute(dataTransfer);

                Toast.makeText(MainActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                rvPlaces.setVisibility(View.VISIBLE);
                noPlaces.setVisibility(View.GONE);
                pa = new PlaceAdapter(places);
                addToFavourites.setVisibility(View.VISIBLE);
                pa.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(edu.dlsu.mobidev.chibog.Place p) {
                        Toast.makeText(getBaseContext(), "User clicked on " + p.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                rvPlaces.setAdapter(pa);
                rvPlaces.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                            LinearLayoutManager.VERTICAL, false));
            }
        });


        pullUp = (LinearLayout) findViewById(R.id.pull_up);
        closeListOfPlaces = (ImageButton) findViewById(R.id.close_list);
        closeListOfFavourites = (ImageButton) findViewById(R.id.close_list_favourites);
        favouritesPullUp = (LinearLayout) findViewById(R.id.favorites);

        addToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                builder.setTitle("Input name for this list");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String favouriteName = input.getText().toString();
                        Log.i("ADDED LIST", places.toString());
                        dbHelper.addLocation(favouriteName, places);
                        Toast.makeText(MainActivity.this, favouriteName
                                + " was added to favourites!", Toast.LENGTH_LONG).show();
                        favouriteAdapter.swapCursor(dbHelper.getAllFavourites());
                        if(dbHelper.getAllFavourites().getCount() > 0){
                            noFavourites.setVisibility(View.GONE);
                        }
                    }
                });

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        // Closes the list of places
        closeListOfPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation bottomDown = AnimationUtils.loadAnimation(getBaseContext(),
                        R.anim.bottom_down);
                hiddenPanel.startAnimation(bottomDown);

                bottomDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mainScreen.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        hiddenPanel.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });


        // Closes the list of favourite places
        closeListOfFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFavourites();
            }
        });


        // Opens the list of places loaded
        pullUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation bottomUp = AnimationUtils.loadAnimation(getBaseContext(),
                        R.anim.bottom_up);
                hiddenPanel.startAnimation(bottomUp);
                hiddenPanel.setVisibility(View.VISIBLE);

                bottomUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mainScreen.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        // Opens the list of favourite places
        favouritesPullUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation bottomUp = AnimationUtils.loadAnimation(getBaseContext(),
                        R.anim.bottom_up);
                hiddenPanelFavourites.startAnimation(bottomUp);
                hiddenPanelFavourites.setVisibility(View.VISIBLE);

                bottomUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mainScreen.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    private void closeFavourites(){
        Animation bottomDown = AnimationUtils.loadAnimation(getBaseContext(),
                R.anim.bottom_down);
        hiddenPanelFavourites.startAnimation(bottomDown);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mainScreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiddenPanelFavourites.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        //builds URL for search
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyDwD_rwM8VjiRZFnPBJKyLy5Eu9Bs57mKA");

        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            //everything is ok
            Log.i("Tag", "Connection Success");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but its resolvable
            Log.i("Tag", "G API not available but resolvable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, 1);
            dialog.show();
        } else {
            Log.i("Tag", "Somethings definitely wrong");
            Toast.makeText(this, "You cant make any Map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void initializeMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    public boolean isPermissionGranted() {
        //return true if permission is granted false otherwise
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        // Asks for permission to use location services
        if (!isPermissionGranted()) {
            Log.i("Tag", "permission not granted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //pass permissions from manifest and request code
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Checks if user has location services enabled
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location services", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Gets the result of the place picker
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(MainActivity.this, data);
                place_name_text.setText(place.getName());
                place_address_text.setText(place.getAddress());
                if (place.getAttributions() == null) {
                    attribution_text.loadData("no attribution", "text/html; charset=utf-8", "UTF-8");
                } else {
                    attribution_text.loadData(place.getAttributions().toString(), "text/html; charset=utf-8", "UTF-8");
                }
            }
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                requestPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setPadding(0, 60, 0, 200);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    private void removeItem(long id){
        dbHelper.deleteData(id);
        favouriteAdapter.swapCursor(dbHelper.getAllFavourites());
        if(dbHelper.getAllFavourites().getCount() == 0){
            noFavourites.setVisibility(View.VISIBLE);
        }
    }

}
