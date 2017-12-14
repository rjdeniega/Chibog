package edu.dlsu.mobidev.chibog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Ira on 26/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String SCHEMA = "places";
    private static final int VERSION = 4;

    // Table Names
    private static final String TABLE_PLACE = "places";
    private static final String TABLE_RESTAURANT = "restaurants";

    // Common Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Places - Column Names
    private static final String PLACE_NAME = "name";

    // Restaurant - Column Names
    private static final String PLACE_ID = "place_id";
    private static final String RESTAURANT_NAME = "name";
    private static final String RESTAURANT_LOCATION = "location";
    private static final String RESTAURANT_LATITUDE = "lat";
    private static final String RESTAURANT_LONGITUDE = "lng";
    private static final String RESTAURANT_VICINITY = "vicinity";

    // Table Create Statements
    // Places Table
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE " + TABLE_PLACE + " ( "
                                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + PLACE_NAME + " TEXT NOT NULL, " + KEY_CREATED_AT
                                        + " DATETIME);";
    // Restaurant Table
    private static final String CREATE_TABLE_RESTAURANT = "CREATE TABLE " + TABLE_RESTAURANT + " ( "
                                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + PLACE_ID + " INTEGER, " + RESTAURANT_NAME + " TEXT, "
                                        + RESTAURANT_LOCATION + " TEXT, " + RESTAURANT_LATITUDE
                                        + " REAL, " + RESTAURANT_LONGITUDE + " REAL, "
                                        + RESTAURANT_VICINITY + " TEXT, " + KEY_CREATED_AT
                                        + " DATETIME);";

    DatabaseHelper(Context context) {
        super(context, SCHEMA, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PLACES);
        sqLiteDatabase.execSQL(CREATE_TABLE_RESTAURANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);

        onCreate(sqLiteDatabase);
    }

    void addLocation(String name, ArrayList<Place> restaurants){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cvPlace = new ContentValues();
        cvPlace.put(PLACE_NAME, name);
        long id = db.insert(TABLE_PLACE, null, cvPlace);

        for (Place restaurant: restaurants) {
            ContentValues cvRestaurant = new ContentValues();
            cvRestaurant.put(PLACE_ID, id);
            cvRestaurant.put(RESTAURANT_NAME, restaurant.getName());
            cvRestaurant.put(RESTAURANT_LOCATION, restaurant.getLocation());
            cvRestaurant.put(RESTAURANT_LATITUDE, restaurant.getLat());
            cvRestaurant.put(RESTAURANT_LONGITUDE, restaurant.getLng());
            cvRestaurant.put(RESTAURANT_VICINITY, restaurant.getVicinity());
            db.insert(TABLE_RESTAURANT, null, cvRestaurant);
        }
    }

    Cursor getAllFavourites(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_PLACE, null, null, null, null, null,
                PLACE_NAME + " ASC");
    }

    void deleteData (long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RESTAURANT, "id =" + id, null);
        db.delete(TABLE_PLACE, "id =" + id, null);
    }

    ArrayList<Place> getRestaurantsFromFavourite(long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor place = db.query(TABLE_PLACE, null, KEY_ID + "=" + id,
                null, null, null, null);
        place.close();
        Cursor result = db.query(TABLE_RESTAURANT, null, PLACE_ID + "=" + id,
                null, null ,null, null);

        ArrayList<Place> places = new ArrayList<>();
        if (result.moveToFirst()){
            while(!result.isAfterLast()){
                Place init = new Place();
                init.setName(result.getString(result.getColumnIndex(RESTAURANT_NAME)));
                init.setLocation(result.getString(result.getColumnIndex(RESTAURANT_LOCATION)));
                init.setVicinity(result.getString(result.getColumnIndex(RESTAURANT_VICINITY)));
                init.setLat(result.getDouble(result.getColumnIndex(RESTAURANT_LATITUDE)));
                init.setLng(result.getDouble(result.getColumnIndex(RESTAURANT_LONGITUDE)));
                places.add(init);
                result.moveToNext();
            }
        }
        result.close();
        return places;
    }
}
