package com.example.khumalo.appui;

import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.example.khumalo.appui.Utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static com.example.khumalo.appui.Utils.Utils.getDriverProfilePicPath;
import static com.example.khumalo.appui.Utils.Utils.saveDriverProfilePicPath;

public class DriverValidation extends AppCompatActivity {

    FirebaseStorage FireStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_validation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FireStorage = FirebaseStorage.getInstance();

        if(getIntent().getBooleanExtra(Constants.DRIVER_PROFILE_VALIDATION_EXTRA,false)){
            downloadDriverProfilePic();
        }else{
            loadBackdrop();
        }

        NotificationManager nMgr = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        nMgr.cancel(Constants.AVAILABLE_DRIVER_NOTIFICATION);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Alicia Keys");


        RatingBar ratingBar = (RatingBar) findViewById(R.id.driver_ratings);
        ratingBar.setRating(4);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(getBaseContext()).load(getDriverProfilePicPath(this)).centerCrop().into(imageView);
        // Got the download URL for 'users/me/profile.png'
    }

    private void downloadDriverProfilePic() {
        StorageReference storageRef = FireStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
        StorageReference pathReference = storageRef.child("alicia_keys.jpg");
        try {
            File file = File.createTempFile("images","jpg");
            saveDriverProfilePicPath(this,file.getAbsolutePath());
            pathReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    if (taskSnapshot.getBytesTransferred() == taskSnapshot.getTotalByteCount()) {
                        loadBackdrop();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("Tag", "File could not be downloaded " + exception.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Tag","File could not be created");
        }

    }
}
