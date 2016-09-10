package com.example.khumalo.appui;

import android.*;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.khumalo.appui.ClientFragments.GoogleMapFragment;
import com.example.khumalo.appui.ClientModel.ClientProfile;
import com.example.khumalo.appui.DriverModel.DriverLocation;
import com.example.khumalo.appui.DriverModel.DriverRoute;
import com.example.khumalo.appui.Login.LoginActivity;
import com.example.khumalo.appui.NotificationCenter.BuildNotification;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.PermissionUtils;
import com.example.khumalo.appui.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final int PLACE_PICKER_REQUEST = 44;
    private ProgressDialog progressDialog;
    static LatLng currentPosition;
    private boolean isDriverNotFound;
    private DriverRoute myDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Utils.isLocationStatusNotSet(this)) {
            Utils.setLocationShareStatus(this, true);
            Utils.setLocationStatuFlag(this, false);
        }
        buildGoogleApiClient();
        if(Utils.getClientKey(this)==null){
            addClient();
        }


        initializeScreen();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildPlacePickerAutoCompleteDialog();

           /*     Firebase firebase = new Firebase(Constants.FIREBASE_NOTIFICATION_TEST_MESSAGE);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        BuildNotification.generateNotification(getBaseContext());
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/
                Snackbar.make(view, "A message has been added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(5000);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(3000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_credit) {
            // Handle the camera action


        } else if (id == R.id.nav_signOut) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.isLoggedIn, false);
            editor.putBoolean(Constants.USER_STATUS, false);
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            Utils.setLocationShareStatus(this, true);
            startActivity(intent);

        } else if (id == R.id.nav_driver_profile){
            Intent intent = new Intent(this,DriverValidation.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        makeMyLocationEnabled(mMap);
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (Utils.isLocationShared(this)) {
            requestLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    /*
* Create a new location client, using the enclosing class to
* handle callbacks.
*/
    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    private void makeMyLocationEnabled(GoogleMap map){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, Constants.MY_LOCATION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        }else {
            map.setMyLocationEnabled(true);
        }
    }


    private void requestLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            Intent intent = new Intent(this, CurrentLocationReceiver.class);

            PendingIntent locationIntent = PendingIntent.getBroadcast(this, 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, mLocationRequest, locationIntent);

        }
    }

    //Permision Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                requestLocationUpdates();
            } else if(requestCode==Constants.MY_LOCATION_REQUEST_CODE) {
                makeMyLocationEnabled(mMap);
                if (Utils.isLocationShared(this)) {
                    requestLocationUpdates();
                }
            }else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE){
                buildPlacePickerAutoCompleteDialog();
            }else {
                mPermissionDenied = true;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }



    ///Building Place AutoComplete without a Dialog
    private void buildPlacePickerAutoCompleteDialog() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, PLACE_AUTOCOMPLETE_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else {
            Log.d("Tag", "The Location Access has been Granted");
            try {
                Toast toast = Toast.makeText(this, "What's your destination?", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                LatLngBounds CapeTown = new LatLngBounds(new LatLng(-34.307222, 18.416507), new LatLng(-30.892878, 24.217288));
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .setBoundsBias(CapeTown)
                                .build(this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK ) {
                Place place = PlaceAutocomplete.getPlace(this, data);
             /*   progressDialog = ProgressDialog.show(this, "Please wait.",
                        "Searching for your ride...!", true);*/
                LatLng destination = place.getLatLng();
               // searchForMyRide(destination);

                Log.d("Tag", place.getAddress().toString());

            }
        }
    }


    public static class CurrentLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            LocationResult result = LocationResult.extractResult(intent);
            if(result!=null){
                double latitude = result.getLastLocation().getLatitude();
                double longitude = result.getLastLocation().getLongitude();
                currentPosition = new LatLng(latitude,longitude);
                Toast.makeText(context, currentPosition.toString(), Toast.LENGTH_SHORT).show();
                DriverLocation clientLocation = new DriverLocation(latitude,longitude);
                addLocation(clientLocation,context);
            }
        }
    }


    private static void addLocation(DriverLocation driverLocation, Context context){
        String keyID = Utils.getClientKey(context);
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.LOCATIONS_URL).child(keyID);
        database.setValue(driverLocation);
    }


    //The real work being done here
    private void searchForMyRide(final LatLng destination){
        Firebase firebase = new Firebase(Constants.FIREBASE_ROUTES_URL);
        final List<DriverRoute> driverRoutes= new ArrayList<DriverRoute>();;
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isDriverNotFound = true;
                Log.d("Tag", "The database returned " + dataSnapshot.getValue().toString() + " of Type " + dataSnapshot.getClass().getName());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DriverRoute driverRoute = new DriverRoute(snapshot.getValue(String.class), snapshot.getKey());
                    if (currentPosition != null) {
                        if (driverRoute.isMatch(currentPosition, destination)) {
                            progressDialog.dismiss();
                            myDriver = driverRoute;
                            isDriverNotFound = false;
                            Toast.makeText(getBaseContext(), "Your ride almost here", Toast.LENGTH_LONG).show();
                            updateMap();
                            Log.d("Tag", "Driver found");
                            break;
                        } else {
                            Log.d("Tag", "This driver does not match");
                        }
                    } else {
                        Log.d("Tag", "Current Positions is null");
                        Toast.makeText(getBaseContext(), "CurrentPosition is null", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        break;
                    }
                }
                if (isDriverNotFound) {
                    Toast.makeText(getBaseContext(), "No Ride yet", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Tag", "loadPost:onCancelled ", firebaseError.toException());
            }
        });

    }



    private void addClient() {
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.CLIENTS_URL);
        Firebase keyRef = database.push();
        String keyID = keyRef.getKey();
        Utils.setClientKey(keyID, this);
        ClientProfile clientProfile = new ClientProfile("Kamatoto","Chinos");
        keyRef.setValue(clientProfile);
    }




    //Updating Map with when available driver is found
    private void updateMap() {
        List<LatLng> polyLocations= myDriver.getWayLatLongPolyline();
        int lastPostion = polyLocations.size()-1;
        addMarkerToDestination(mMap,polyLocations,lastPostion);
        addMarkerToDestination(mMap,polyLocations,0);
        drawPolylineCurrentPlaceToDestanation(mMap, polyLocations);
        moveCameraToPosition(mMap, polyLocations, lastPostion);
    }


    private void addMarkerToDestination(GoogleMap mMap, List<LatLng> polyLocations, int finalPosition) {

        MarkerOptions destination = new MarkerOptions().position(polyLocations.get(finalPosition))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        mMap.addMarker(destination);
    }


    private void drawPolylineCurrentPlaceToDestanation(GoogleMap mMap, List<LatLng> polyLocations) {
        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        polylineOptions.addAll(polyLocations);
        mMap.addPolyline(polylineOptions);
    }


    private void moveCameraToPosition(GoogleMap mMap, final List<LatLng> polyLocations, int finalPosition) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(polyLocations.get(0)).include(polyLocations.get(finalPosition));
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 4000, null);

    }


    //Options Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem bedMenuItem = menu.findItem(R.id.action_settings);
        if(Utils.isLocationShared(this)){
            bedMenuItem.setTitle("Disable Location Share");
            return true;
        }else{
            bedMenuItem.setTitle("Enable Location Share");
            return true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(Utils.isLocationShared(this)) {
                Log.d("Tag", "The button to stop has been pressed");
                if (this.mGoogleApiClient != null) {
                    Intent intent = new Intent(this, CurrentLocationReceiver.class);
                    PendingIntent locationIntent = PendingIntent.getBroadcast(this, 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationIntent);
                    item.setTitle("Enable Location Share");
                    Utils.setLocationShareStatus(this,false);

                }
            }else {
                if (this.mGoogleApiClient != null&& mGoogleApiClient.isConnected()) {
                    requestLocationUpdates();
                    item.setTitle("Disable Location Share");
                    Utils.setLocationShareStatus(this,true);
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }


}
