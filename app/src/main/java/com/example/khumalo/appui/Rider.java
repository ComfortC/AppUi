package com.example.khumalo.appui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.khumalo.appui.BackgroundServices.BackgroundLocationService;
import com.example.khumalo.appui.DriverModel.DriverLocation;
import com.example.khumalo.appui.DriverModel.DriverProfile;
import com.example.khumalo.appui.Login.LoginActivity;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.PermissionUtils;
import com.example.khumalo.appui.Utils.Utils;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.khumalo.appui.Utils.Utils.getClientFullName;
import static com.example.khumalo.appui.Utils.Utils.getImageUriString;
import static com.example.khumalo.appui.Utils.Utils.getPolyLineCode;
import static com.example.khumalo.appui.Utils.Utils.isLocationShared;
import static com.google.maps.android.PolyUtil.decode;

public class Rider extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final String Tag = "Tag";
    private static final int CURRENT_PLACE_PERMISSION_REQUEST = 2;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final int PLACE_PICKER_REQUEST = 44;
    private boolean mPermissionDenied = false;
    Location mLastLocation;
    static String current_Place_extra;
    String destination;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildGoogleClient();
        if(Utils.getDriverKey(this)==null){
           addDriver();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap!=null){
                    mMap.clear();
                }

                buildPlacePickerAutoCompleteDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.driver_full_name);
        ImageView profilePic = (ImageView)hView.findViewById(R.id.circleView);
        Glide.with(this).load(getImageUriString(this))
                .asBitmap()
                .into(new BitmapImageViewTarget(profilePic) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                       // saveImageToFireBaseDatabase(bitmap);
                    }
                });



        nav_user.setText(getClientFullName(this));
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void addDriver() {
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.DRIVERS_URL);
        Firebase keyRef = database.push();
        String keyID = keyRef.getKey();
        Utils.setDriverKey(keyID, this);
        DriverProfile driver = new DriverProfile("Comfort","Chinondiwana","CY 30052",074532255, "sssss");
        keyRef.setValue(driver);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        makeMyLocationEnabled(mMap);
    }


    ////////GoogleClientImplementation

    ////////////
    protected synchronized void buildGoogleClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Tag, "Connection has failed");
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Tag, "The client has been connected");

        if (Utils.isLocationShared(this)) {
            requestLastKnownLocation();

    }

    }

    private void makeMyLocationEnabled(GoogleMap map){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

        }else {
            map.setMyLocationEnabled(true);
        }
    }


    private void requestLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, CURRENT_PLACE_PERMISSION_REQUEST,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);

        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            current_Place_extra = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
            Log.d("Tag", "Place co-ordinates are " + current_Place_extra);
            Intent intent = new Intent(this, BackgroundLocationService.class);
            startService(intent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (requestCode == CURRENT_PLACE_PERMISSION_REQUEST) {

                Log.d(Tag, "The Location Access has been Granted");
                    requestLastKnownLocation();
                    if(mMap!=null) {
                        makeMyLocationEnabled(mMap);
                    }
            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    buildPlacePickerAutoCompleteDialog();


            } else {
                mPermissionDenied =true;
            }

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
            Log.d(Tag, "The Location Access has been Granted");
            try {
                Toast toast = Toast.makeText(getBaseContext(), "What's your destination?", Toast.LENGTH_LONG);
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



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                destination = place.getId();
                progressDialog = ProgressDialog.show(this, "Please wait.",
                        "Searching for your ride...!", true);
                new DownloadRawData().execute();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Tag, "The connection has been suspended");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rider, menu);
        return true;
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(isLocationShared(this)){
                Log.d(Tag, "The button to stop has been pressed");
                stopService(new Intent(this, BackgroundLocationService.class));
                item.setTitle("Enable Location Share");
                Utils.setLocationShareStatus(this, false);
            }else {
                startService(new Intent(this, BackgroundLocationService.class));
                item.setTitle("Disable Location Share");
                Utils.setLocationShareStatus(this, true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("Tag", "AsyncTask Called");
            final String ORIGIN_PARAM = "origin";
            final String DESTINATION_PARAM = "destination";
            final String KEY_PARAM = "key";
            final String REGION_PARAM = "region";

            final String PLACE_ID_PREFIX = "place_id:" + destination;

            Uri builtUri = Uri.parse(Constants.FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(ORIGIN_PARAM, current_Place_extra)
                    .appendQueryParameter(DESTINATION_PARAM, PLACE_ID_PREFIX)
                    .appendQueryParameter(REGION_PARAM, Constants.REGION_PARAM)
                    .appendQueryParameter(KEY_PARAM, getBaseContext().getString(R.string.ApiKey)).build();
            String Directions = DownloadDirections(builtUri.toString());

            return Directions;
        }

        @Override
        protected void onPostExecute(String res) {
            String thisLocationToDestination="";
            List<LatLng> polylineToDestination;
            try {
                thisLocationToDestination= getPolyLineCode(res);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
            if(!thisLocationToDestination.equals("")) {
                insertRouteIntoTheDatabase(thisLocationToDestination);
                polylineToDestination = decode(thisLocationToDestination);
                int lastPosition = polylineToDestination.size() - 1;
                drawPolylineCurrentPlaceToDestanation(mMap, polylineToDestination);
                addMarkerToDestination(mMap, polylineToDestination, lastPosition);
                addMarkerToDestination(mMap, polylineToDestination, 0);
                moveCameraToPosition(mMap, polylineToDestination, lastPosition);
            }
        }
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


    private void insertRouteIntoTheDatabase(String thisLocationToDestination) {
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.ROUTES_URL);
        String keyID = Utils.getDriverKey(getBaseContext());
        database.child(keyID).setValue(thisLocationToDestination);
    }


    private void moveCameraToPosition(GoogleMap mMap, final List<LatLng> polyLocations, int finalPosition) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(polyLocations.get(0)).include(polyLocations.get(finalPosition));
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 4000,null);

    }

    private String DownloadDirections(String Uri){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {

            URL url = new URL(Uri);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return "";
            }

            return buffer.toString();


        } catch (IOException e) {
            Log.e("Tag", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return " ";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Tag", "Error closing stream", e);
                }
            }
        }
    }


    public static class LocationReceiver extends BroadcastReceiver {




        @Override
        public void onReceive(Context context, Intent intent) {

            LocationResult result = LocationResult.extractResult(intent);
            if(result!=null){
                double latitude = result.getLastLocation().getLatitude();
                double longitude = result.getLastLocation().getLongitude();
                current_Place_extra = String.valueOf(latitude)+","+String.valueOf(longitude);
                LatLng currentPosition = new LatLng(latitude,longitude);
                Toast.makeText(context,currentPosition.toString(),Toast.LENGTH_SHORT).show();
                DriverLocation here = new DriverLocation(latitude, longitude);
                addLocation(here,context);
            }
        }


    }

    private static void addLocation(DriverLocation driverLocation, Context context){
        String keyID = Utils.getDriverKey(context);
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.LOCATIONS_URL).child(keyID);
        database.setValue(driverLocation);
    }



}
