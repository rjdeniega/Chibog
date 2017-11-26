package edu.dlsu.mobidev.chibog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ira on 26/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String SCHEMA = "places";
    private static final int VERSION = 1;

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
    private static final String RESTAURANT_LONGTITUDE = "lng";

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
                                        + " REAL, " + RESTAURANT_LONGTITUDE + " REAL);";

    public DatabaseHelper(Context context) {
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
}
