package com.example.keiichi.androidopdracht;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private MapView map;
    private Button searcButton;
    private TextView searchField;
    private Button listButton;
    private Button removeButton;
    private String urlSearch = "http://nominatim.openstreetmap.org/search?q=";
    private String urlZones = "http://datasets.antwerpen.be/v4/gis/paparkeertariefzones.json";
    private RequestQueue requestQue;
    final ArrayList<OverlayItem> items = new ArrayList<>();
    private LatLng location;
    private ArrayList<LatLng> locaties;


    SqlLitehelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }

        helper = new SqlLitehelper(this);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(18);
        map.getController().setCenter(new GeoPoint(51.103704,4.5029093));

        requestQue = Volley.newRequestQueue(this);
        searchField = (TextView)findViewById(R.id.search_txtview);
        searcButton = (Button)findViewById(R.id.search_button);
        removeButton = (Button)findViewById(R.id.remove_button);
        searcButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String searchString = "";
                try {
                    searchString = URLEncoder.encode(searchField.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JsonArrayRequest jr = new JsonArrayRequest(urlSearch + searchString + "&format=json", new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject obj = response.getJSONObject(0);
                            GeoPoint g = new GeoPoint(obj.getDouble("lat"), obj.getDouble("lon"));
                            map.getController().setCenter(g);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("waa",error.getMessage());
                    }
                });
                requestQue.add(jr);
            }
        });
        removeButton.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                try{
                    helper.deleteDB();
                    Toast.makeText(MainActivity.this, "DB deleted", Toast.LENGTH_LONG).show();
                    map.getOverlays().clear();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }));

        //Todo: hier is de swipe methode voor db te deleten, deze werkt maar zit vast op 1 locatie
        /*map.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                try{
                    helper.deleteDB();
                    Toast.makeText(MainActivity.this, "DB deleted", Toast.LENGTH_LONG).show();
                    map.getOverlays().clear();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
            /*public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public void onResume(){
        super.onResume();


        try{
            locaties = helper.getLocations();

            for (LatLng locatie: locaties) {
                addMarker(new GeoPoint(locatie.latitude,locatie.longitude));
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    e.getMessage() , Toast.LENGTH_LONG).show();
        }




    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_UP){
            Projection proj = this.map.getProjection();
            GeoPoint g = (GeoPoint) proj.fromPixels((int)event.getX(),(int)event.getY() - (searchField.getHeight() * 2));
            Intent nextScreenIntent = new Intent(this,EnterMarker.class);
            location = new LatLng(g.getLatitude(),g.getLongitude());






            nextScreenIntent.putExtra("location", location);
            startActivity(nextScreenIntent);


        }

        return super.onTouchEvent(event);
    }



    private void addMarker(GeoPoint g){
        OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Current Position",g);
        Drawable myCurrentLocationMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default,null);
        myLocationOverlayItem.setMarker(myCurrentLocationMarker);

        items.add(myLocationOverlayItem);
        DefaultResourceProxyImpl resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

        ItemizedIconOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        return true;
                    }

                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return true;
                    }
                }, resourceProxy);
        this.map.getOverlays().add(currentLocationOverlay);


        this.map.invalidate();
    }


    private void checkPermission() {
        List<String> permissions = new ArrayList<>();
        String message = "osmdroid permissions:";
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            message += "\nStorage access to store map tiles.";
        }
        if(!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } // else: We already have permissions, so handle as normal
    }
}
