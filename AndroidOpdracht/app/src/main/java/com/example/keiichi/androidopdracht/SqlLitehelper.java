package com.example.keiichi.androidopdracht;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Keiichi on 6/10/2017.
 */

/**
 * Created by Keiichi on 6/10/2017.
 */

public class SqlLitehelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "marker.db";
    private static final String TABLE_MARKERS = "markers";
    private static final int DATABASE_VERSION = 6;
    String[] coordinates = new String[2];

    public SqlLitehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MARKER_TABLE = "CREATE TABLE " + TABLE_MARKERS + "(_id INTEGER PRIMARY KEY, lat DOUBLE, lon DOUBLE, beschrijving VARCHAR(500))";
        sqLiteDatabase.execSQL(CREATE_MARKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(sqLiteDatabase);
    }

    public void addLocation(double lat, double lon,String beschrijving) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_LOCATIONS, null, null);

        ContentValues values = new ContentValues();
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("beschrijving", beschrijving);

        db.insert(TABLE_MARKERS, null, values);
        db.close();
    }
    public void deleteDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MARKERS,null,null);
        String CREATE_MARKER_TABLE = "CREATE TABLE " + TABLE_MARKERS + "(_id INTEGER PRIMARY KEY, lat DOUBLE, lon DOUBLE, beschrijving VARCHAR(500))";


       this.getWritableDatabase();
    }

    public ArrayList<LatLng> getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<LatLng> list = new ArrayList<>();
        double aDouble;

        /**String tblName       The table name to compile the query against.
         String[] columnNames   A list of which table columns to return. Passing "null" will return all columns.
         String whereClause         Where-clause, i.e. filter for the selection of data, null will select all data.
         String[] selectionArgs     You may include ?s in the "whereClause"". These placeholders will get replaced by the values from the selectionArgs array.
         String[] groupBy       A filter declaring how to group rows, null will cause the rows to not be grouped.
         String[] having            Filter for the groups, null means no filter.
         String[] orderBy       Table columns which will be used to order the data, null means no ordering.**/

        Cursor  cursor = db.rawQuery("select * from " + TABLE_MARKERS,null );
        cursor.moveToFirst();
        aDouble = cursor.getCount();


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                list.add(new LatLng(cursor.getDouble(cursor.getColumnIndex("lat")),cursor.getDouble(cursor.getColumnIndex("lon"))));

                cursor.moveToNext();
            }
        }
        cursor.close();

        return list;
        //Todo : button toevoegen om list weer te geven in activity
    }
    public String[] getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        /**String tblName 		The table name to compile the query against.
         String[] columnNames 	A list of which table columns to return. Passing "null" will return all columns.
         String whereClause 		Where-clause, i.e. filter for the selection of data, null will select all data.
         String[] selectionArgs 	You may include ?s in the "whereClause"". These placeholders will get replaced by the values from the selectionArgs array.
         String[] groupBy 		A filter declaring how to group rows, null will cause the rows to not be grouped.
         String[] having 			Filter for the groups, null means no filter.
         String[] orderBy 		Table columns which will be used to order the data, null means no ordering.**/

        Cursor cursor = db.query(TABLE_MARKERS,
                new String[] { "_id", "lat", "lon" },
                "_id=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();


        for (int i = 0; i < 2; i++) {
            coordinates[i] = cursor.getString(i + 1);
        }

        return coordinates;
    }


}
