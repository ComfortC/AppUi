package com.example.khumalo.appui.ClientFragments;


import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.khumalo.appui.BackgroundServices.BackgroundLocationService;
import com.example.khumalo.appui.ClientModel.ClientProfile;
import com.example.khumalo.appui.DriverModel.*;
import com.example.khumalo.appui.MainActivity;
import com.example.khumalo.appui.R;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.PermissionUtils;
import com.example.khumalo.appui.Utils.Utils;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GoogleMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

   //Map SetUp
    private SupportMapFragment mSupportMapFragment;
    GoogleMap m_map;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GoogleMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoogleMapFragment newInstance() {
        GoogleMapFragment fragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem bedMenuItem = menu.findItem(R.id.action_settings);
        if(Utils.isLocationShared(getContext())){
            bedMenuItem.setTitle("Disable Location Share");
        }else{
            bedMenuItem.setTitle("Enable Location Share");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(Utils.isLocationShared(getContext())) {
                Log.d("Tag", "The button to stop has been pressed");
                if (this.mGoogleApiClient != null) {
                    Intent intent = new Intent(getContext(), CurrentLocationReceiver.class);
                    PendingIntent locationIntent = PendingIntent.getBroadcast(getContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationIntent);
                    item.setTitle("Enable Location Share");
                    Utils.setLocationShareStatus(getContext(),false);

                }
            }else {
                if (this.mGoogleApiClient != null&& mGoogleApiClient.isConnected()) {
                    requestLocationUpdates();
                    item.setTitle("Disable Location Share");
                    Utils.setLocationShareStatus(getContext(),true);
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    m_map = googleMap;
                    makeMyLocationEnabled(m_map);
                }
            });


        }

          if(Utils.getClientKey(getContext())==null){
            addClient();
          }

        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(5000);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(3000);
        buildGoogleApiClient();
        return rootView;
    }


    private void addClient() {
        Firebase database = new Firebase(Constants.FIREBASE_URL).child(Constants.CLIENTS_URL);
        Firebase keyRef = database.push();
        String keyID = keyRef.getKey();
        Utils.setClientKey(keyID, getContext());
        ClientProfile clientProfile = new ClientProfile("Kamatoto","Chinos");
        keyRef.setValue(clientProfile);
    }


    /*
* Create a new location client, using the enclosing class to
* handle callbacks.
*/
    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

        @Override
        public void onDestroy () {
            super.onDestroy();

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

    @Override
    public void onConnected(Bundle bundle) {
        // Request location updates using static settings
        if (Utils.isLocationShared(getContext())) {
            requestLocationUpdates();

        }
    }

   private void makeMyLocationEnabled(GoogleMap map){
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), Constants.MY_LOCATION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        }else {
            map.setMyLocationEnabled(true);
        }
    }

    private void requestLocationUpdates() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            Intent intent = new Intent(getContext(), CurrentLocationReceiver.class);

            PendingIntent locationIntent = PendingIntent.getBroadcast(getContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, mLocationRequest, locationIntent);

        }
    }

   //Permision Results
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
       if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
           if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                   Manifest.permission.ACCESS_FINE_LOCATION)) {

               requestLocationUpdates();

           }
       } else if(requestCode==Constants.MY_LOCATION_REQUEST_CODE) {

           makeMyLocationEnabled(m_map);
       }else {
           mPermissionDenied = true;
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
                .newInstance(true).show(getChildFragmentManager(), "dialog");
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public static class CurrentLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            LocationResult result = LocationResult.extractResult(intent);
            if(result!=null){
                double latitude = result.getLastLocation().getLatitude();
                double longitude = result.getLastLocation().getLongitude();
                LatLng currentPosition = new LatLng(latitude,longitude);
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

}

