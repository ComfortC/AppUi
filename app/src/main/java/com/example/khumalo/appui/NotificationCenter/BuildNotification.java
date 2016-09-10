package com.example.khumalo.appui.NotificationCenter;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.TaskStackBuilder;
import android.view.Display;
import android.widget.MultiAutoCompleteTextView;

import com.example.khumalo.appui.DriverValidation;
import com.example.khumalo.appui.R;
import com.example.khumalo.appui.Utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KHUMALO on 8/21/2016.
 */
public class BuildNotification  {



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public static void generateNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_car)
                .setContentTitle("Your Ride has arrived")
                .setContentText("Mr Comfort Chinondiwans is arround to share a ride with you")
                .setSound(new RingtoneManager(context).getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, DriverValidation.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DriverValidation.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent result = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(result);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(Constants.AVAILABLE_DRIVER_NOTIFICATION,builder.build());

    }


}
