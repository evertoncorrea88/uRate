package everton.urate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.google.android.gms.maps.model.MarkerOptions;

public class MapDisplayCategoryActivity extends FragmentActivity{

    private static final String TAG = "MapActivity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private MyApplication myApp;
    private Spinner spinMapCategory;

    GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display_category);
        setUpMapIfNeeded();
    }



    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.889, -87.622), 16));

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_urate))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(41.889, -87.622)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        myApp = (MyApplication) getApplication();
        spinMapCategory =  (Spinner) findViewById(R.id.spin_map_category);

        ArrayAdapter<String> adapter =  new ArrayAdapter(this, android.R.layout.simple_spinner_item, myApp.listGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMapCategory.setPrompt("Select a Category");
        spinMapCategory.setAdapter(adapter);

        spinMapCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.clear();
                List<Item> itemlist = myApp.listItem.get(myApp.listGroup.get(position));
                for (final Item i : itemlist) {
                    String lat = i.getLat();
                    String lng = i.getLng();
                    String name = i.getName();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name));
                    mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            Toast.makeText(getApplicationContext(), i.getName().toString() +  i.getCategory(), Toast.LENGTH_LONG).show();
                           // Intent intent = new Intent(Map.this,OtherActivity.class);
                            //startActivity(intent);

                            return false;
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_LONG).show();

            }
        });

        setUpMapIfNeeded();
    }

    private void setUpMap() {
        gps = new GPS(MapDisplayCategoryActivity.this);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //Location location = new Location(gps.getLocation());

            LatLng latLng = new LatLng(latitude, longitude);
            float zoomLevel = 15.0f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            mMap.animateCamera(cameraUpdate);

            getAddressFromLocation(gps.getLocation(), this, new GeocoderHandler());
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " +
                    longitude, Toast.LENGTH_LONG).show();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));


        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_display_category))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public static void getAddressFromLocation(
            final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            // replace by what you need to do
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
        }



    }

}

