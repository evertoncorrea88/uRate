package everton.urate;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CheckBox;
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

// Added by PTR 12/09. This class is opened by the map button on the ItemDetailActivity.
// It gets and displays the users location and allows the user to move the marker if
// necessary for correction. It then returns the address to be displayed on ItemDetail.
public class MapGetLocationActivity extends FragmentActivity {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Fragment fragment; // when will this get used.
    // GPS class
    GPS gps;
    // private UiSettings mUiSettings;
    // private CheckBox checkbox;
    //EditText mapSearchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_get_location);


        setUpMapIfNeeded();
    }

    /**
     * Returns whether the checkbox with the given id is checked.
     */
    private boolean isChecked(int id) {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // Added PTR 12/09. This button returns the address to ItemDetail onces the
        // marker is set

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_get_location))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        gps = new GPS(MapGetLocationActivity.this);

        // acm 12/17
        final String TAG_LAT = "LAT";
        final String TAG_LONG = "LONGITUDE";

        // check if GPS enabled
        if(gps.canGetLocation()){
            // to resolve
            //     java.lang.IllegalStateException: MyLocation layer not enabled

            mMap.setMyLocationEnabled(true);

            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();
            //Location location = new Location(gps.getLocation());

            final LatLng latLng = new LatLng(latitude, longitude);
            float zoomLevel = 15.0f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            mMap.animateCamera(cameraUpdate);
            //MarkerOptions marker = new MarkerOptions();
            //marker.position(latLng);

            getAddressFromLocation(gps.getLocation(), this, new GeocoderHandler());
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " +
            //  longitude, Toast.LENGTH_LONG).show();
            final MarkerOptions mOptions = (new MarkerOptions().position(latLng).title("Tap and hold marker to move it").draggable(true));
            mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_urate));
            final LatLng markerPosition = mOptions.getPosition();
            mMap.addMarker(mOptions).showInfoWindow();

            // attempt to drag and save
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {
                    // TODO Auto-generated method stub
                    // Here your code
                    Toast.makeText(MapGetLocationActivity.this, "Dragging Start",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    LatLng position = marker.getPosition(); //
                    Toast.makeText(
                            MapGetLocationActivity.this,
                            "Lat " + position.latitude + " "
                                    + "Long " + position.longitude,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    // TODO Auto-generated method stub
                    // Toast.makeText(MainActivity.this, "Dragging",
                    // Toast.LENGTH_SHORT).show();
                    System.out.println("Draagging");
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


                @Override
                public boolean onMarkerClick(Marker marker) {


                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    LatLng position = marker.getPosition(); //


                    markerOptions.position(position);

                    // Placing a marker on the touched position
                    double lat = position.latitude;
                    double longitude = position.longitude;
                    String LAT = String.valueOf(lat);
                    String LONGITUDE = String.valueOf(longitude);
                    Intent in = new Intent(getApplicationContext(),
                            ItemDetailActivity.class);

                    String result = null;

                    // Sending lat/long to next activity
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                       List <Address> list = null;
                        list = geocoder.getFromLocation(Double.parseDouble(LAT),Double.parseDouble(LONGITUDE),1);
                        if (list != null && list.size() > 0) {
                            Address address = list.get(0);
                            // sending back first address line and locality
                            result = address.getAddressLine(0) + ", " + address.getLocality();

                    }
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible to connect to Geocoder", e);
                    }



                    in.putExtra("address",result );
                    in.putExtra(TAG_LAT, LAT);
                    in.putExtra(TAG_LONG, LONGITUDE);
                    // Toast.makeText(MapGetLocationActivity.this,"Passing Data: Lat" +
                    /// LAT + "/nAnd Long: " + LONGITUDE, Toast.LENGTH_LONG).show();

                    setResult(RESULT_OK,in);
                    finish();


                    // }   ^^ (marker.equals(mMap))


                    return true;
                }
            });

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
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
    String addressresult;
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            //String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    addressresult = bundle.getString("address");
                    break;
                default:
                    addressresult = null;
            }
            // replace by what you need to do
            //  Toast.makeText(getApplicationContext(), addressresult, Toast.LENGTH_LONG);
        }



    }

}

