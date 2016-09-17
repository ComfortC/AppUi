package com.example.khumalo.appui.BackgroundServices;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.khumalo.appui.DriverModel.DriverRoute;
import com.example.khumalo.appui.MainActivity;
import com.example.khumalo.appui.NotificationCenter.BuildNotification;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import static com.example.khumalo.appui.Utils.Utils.setClientReceivedDriverKey;
import static com.example.khumalo.appui.Utils.Utils.setPolylineString;

/**
 * Created by KHUMALO on 9/12/2016.
 */
public class RoutesListener extends Service {

    protected static final String TAG = "route-listener-service";
    private ValueEventListener mActiveListRefListener;
    Firebase firebaseRef;
    private DriverRoute myDriver;
    private boolean driverFound;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseRef = new Firebase(Constants.FIREBASE_ROUTES_URL);
        //Log.d("Tag", "RoutesListener Service has been created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getBaseContext(), "You'l be notified of ride soon!", Toast.LENGTH_LONG).show();
        mActiveListRefListener =  firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DriverRoute driverRoute = new DriverRoute(snapshot.getValue(String.class), snapshot.getKey());
                    if (Utils.getClientLocation(getBaseContext()) != null) {
                        if (driverRoute.isMatch(Utils.getClientLocation(getBaseContext()), Utils.getClientDestination(getBaseContext()))) {
                            myDriver = driverRoute;
                            setClientReceivedDriverKey(getBaseContext(), myDriver.getKey());
                            setPolylineString(getBaseContext(),myDriver.getRoutePolylineCode());
                            firebaseRef.removeEventListener(mActiveListRefListener);
                            BuildNotification.generateNotification(getBaseContext());
                            Intent broadcast =  new Intent();
                            broadcast.setAction("com.example.khumalo.dire.BROADCAST_ACTION");
                            sendBroadcast(broadcast);
                            stopSelf();
                            break;
                        } else {

                        }
                    } else {
                        Log.d("Tag", "Current Positions is null");
                        Toast.makeText(getBaseContext(), "CurrentPosition is null", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Tag","Service is being destroyed");
        if (mActiveListRefListener != null) {
            firebaseRef.removeEventListener(mActiveListRefListener);
        }
        super.onDestroy();
    }


}
