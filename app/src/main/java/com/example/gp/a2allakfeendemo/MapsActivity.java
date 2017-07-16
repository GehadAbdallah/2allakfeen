package com.example.gp.a2allakfeendemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import static android.widget.Toast.makeText;
import static com.example.gp.a2allakfeendemo.R.id.map;
import static com.example.gp.a2allakfeendemo.WelcomeActivity.controller;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private EditText busNumber;
    private ImageButton trackButton;
    private FloatingActionButton fab;
    private TabLayout.Tab sourceTab;
    private TabLayout.Tab destTab;
    private TabLayout tabLayout;
    private LatLng user_location; //added for tracker test
    private boolean firstAppEnter = false;
    private ArrayList<Marker> Markers = new ArrayList<Marker>();

    public LatLng DestinationLocation;
    public LatLng SourceLocation;
    private Marker destMarker;
    Geocoder geocoder;

    Location mLastLocation;
    Marker sourceMarker;
    LocationRequest mLocationRequest;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this,Locale.getDefault());

    /* Create MyLocation button*/
        View myLocationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        if (myLocationButton != null && myLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                    getResources().getDisplayMetrics());
            params.setMargins(0, 0, margin, margin);

            myLocationButton.setLayoutParams(params);
        }
        /*Create busNumber and track buttons*/
        busNumber = (EditText) findViewById(R.id.TrackEditText);
        trackButton = (ImageButton) findViewById(R.id.TrackButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        sourceTab = tabLayout.getTabAt(0);
        destTab = tabLayout.getTabAt(1);
        Log.e("sourceTab",sourceTab.getText().toString());

        /* ATTENTION: This was auto-generated to implement the App Indexing API.
        See https://g.co/AppIndexing/AndroidStudio for more information.*/
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Log.v("Maps","user= "+Controller.currentUser);
//        SignIn.controller = new Controller();

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        firstAppEnter = true;
        sourceTab.select();
        /*Enable MyLocation button*/
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }


    /*Listener that is called when user click on map
    which add marker on clicked position and show Go button*/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //
                if(destTab.isSelected()) {
                    //save current location
                    DestinationLocation = point;
                    Markers.clear();

                    //remove previously placed Marker
                    if (destMarker != null) {
                        destMarker.remove();
                    }

                    //place marker where user just clicked
                    destMarker = mMap.addMarker(new MarkerOptions().position(point).title("Destination Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(DestinationLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                }
                if (sourceTab.isSelected()){
                    //save current location
                    SourceLocation = point;
                    Markers.clear();

                    //remove previously placed Marker
                    if (sourceMarker != null) {
                        sourceMarker.remove();
                    }

                    //place marker where user just clicked
                    sourceMarker = mMap.addMarker(new MarkerOptions().position(point).title("Source Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(SourceLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
                if (SourceLocation != null&& DestinationLocation !=null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(SourceLocation);
                    builder.include(DestinationLocation);
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                }
            /*Listener on Go button, Call results page*/
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SourceLocation != null && DestinationLocation != null) {
                            try {
                                GC gc = new GC(getApplicationContext());
                                //LatLng lls = new LatLng(30.0697625,31.2807798);//al abbasya
                                LatLng lls = new LatLng(29.848824, 31.334252);//Helwan
                                LatLng lle = new LatLng(30.062023, 31.337308);//MadinetNasr
                                controller.GetSuggestions(lls, lle, v, getApplicationContext());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            makeText(v.getContext(), "Enter your Destination location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                if (sourceMarker != null)
                    sourceMarker.remove();
                sourceMarker = mMap.addMarker(markerOptions);
                SourceLocation = sourceMarker.getPosition();
                //TODO:show "Your location" in search bar if Source is active
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                return false;
            }
        });


    /*Create PlaceAutoComplete search bar*/
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.e("Tag", "Place: " + place.getName());
                Log.e("search bar",place.getName().toString());
                if(sourceTab.isSelected()){
                    SourceLocation = place.getLatLng();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(SourceLocation);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    if (sourceMarker != null)
                        sourceMarker.remove();
                    sourceMarker = mMap.addMarker(markerOptions);
                    SourceLocation = sourceMarker.getPosition();
                    //TODO:show "Your location" in search bar if Source is active
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(SourceLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
                if (destTab.isSelected()) {
                    DestinationLocation = place.getLatLng();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(DestinationLocation);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    if (destMarker != null)
                        destMarker.remove();
                    destMarker = mMap.addMarker(markerOptions);
                    SourceLocation = destMarker.getPosition();
                    //TODO:show "Your location" in search bar if Source is active
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(DestinationLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
                if (SourceLocation != null&& DestinationLocation !=null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(SourceLocation);
                    builder.include(DestinationLocation);
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
        Log.v("Maps", "user= " + Controller.currentUser);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        //should get the source from the search bar and check on it also
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busNumber_text = busNumber.getText().toString();
                if (!busNumber_text.isEmpty() && !busNumber_text.equals(null)) {
                    //call Track function and send the map to be rendered and the bus number to be tracked
                    if (SourceLocation != null)
                        controller.Track(mMap, busNumber.getText().toString(), SourceLocation, v);
                    else {
                        Toast toast = Toast.makeText(v.getContext(), "Source location not entered.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else{
                    Toast toast = makeText(v.getContext(), "Enter Bus Number", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API)
                .build();
        client.connect();
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
            LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location){
        //Gehad
//        user_location = new LatLng(location.getLatitude(), location.getLongitude())
        //Eman&Shaza

        mLastLocation = location;
        if(firstAppEnter) {
            if (sourceMarker != null) {
                sourceMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            sourceMarker = mMap.addMarker(markerOptions);
            SourceLocation = sourceMarker.getPosition();
            //TODO:show "Your location" in search bar if Source is active
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            //stop location updates
            if (client != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
            firstAppEnter = false;
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.gp.a2allakfeendemo/http/host/path")
        );

        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Maps Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.gp.a2allakfeendemo/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }




}
