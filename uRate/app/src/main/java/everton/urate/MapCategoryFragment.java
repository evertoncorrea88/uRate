package everton.urate;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapCategoryFragment extends Fragment {

    private SupportMapFragment fragment;
    private GoogleMap map;
    GPS gps;

    private MyApplication myApp;
    private Spinner spinMapCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_category, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();

        myApp = (MyApplication) getActivity().getApplication();
        spinMapCategory =  (Spinner) getView().findViewById(R.id.spin_map_category);

        ArrayAdapter<String> adapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, myApp.listGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMapCategory.setPrompt("Select a Category");
        spinMapCategory.setAdapter(adapter);

        spinMapCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                map.clear();
                List<Item> itemList = myApp.listItem.get(myApp.listGroup.get(position));
                for (Item i : itemList) {
                    String lat = i.getLat();
                    String lng = i.getLng();
                    String name = i.getName();
                    final long itemId = i.getId();
                    float zoomLevel = 15.0f;

                    // added acm 12/17
                    // needs a try catch block or will crash, we need throw an exception for users
                    // who have not added an address from the map and just wrote one by hand, which the
                    // lat and long values are going to be blank.

                    try {
                        MarkerOptions newMarker = new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                .title(name);
                        map.addMarker(newMarker);
                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                    Intent intent = new Intent("everton.urate.ITEM_DETAIL");
                                    intent.putExtra("isEditMode", false);
                                    intent.putExtra("groupPosition", position);
                                    intent.putExtra("childPosition", itemId);
                                    startActivity(intent);
                                return true;
                            }
                        });
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                   // map.animateCamera(cameraUpdate);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(getApplicationContext(), "Select a category", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = fragment.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        gps = new GPS(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //Location location = new Location(gps.getLocation());

            LatLng latLng = new LatLng(latitude, longitude);
            float zoomLevel = 15.0f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            map.animateCamera(cameraUpdate);

            //MarkerOptions marker = new MarkerOptions();
            //marker.position(latLng);

            getAddressFromLocation(gps.getLocation(), getActivity(), new GeocoderHandler());
            // \n is for new line
//            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " +
//                    longitude, Toast.LENGTH_LONG).show();

            map.addMarker(new MarkerOptions().position(latLng).title("Marker"));

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
                    Log.e("Map message" , "Impossible to connect to Geocoder", e);
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
//            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG);
        }
    }

}