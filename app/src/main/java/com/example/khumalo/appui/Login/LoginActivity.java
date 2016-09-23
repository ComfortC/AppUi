package com.example.khumalo.appui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.khumalo.appui.BaseActivity;
import com.example.khumalo.appui.MainActivity;
import com.example.khumalo.appui.MainEntry;
import com.example.khumalo.appui.R;
import com.example.khumalo.appui.Utils.Constants;
import com.example.khumalo.appui.Utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import static com.example.khumalo.appui.Utils.Utils.saveClientFullName;
import static com.example.khumalo.appui.Utils.Utils.saveImageUriString;

public class LoginActivity extends BaseActivity {


    public static final int RC_GOOGLE_LOGIN = 1;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mAuthProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuthProgressDialog.dismiss();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constants.isLoggedIn,true);
                    editor.commit();
                    Utils.setLocationShareStatus(getBaseContext(), true);
                    saveClientFullName(getBaseContext(), user.getDisplayName());
                    Uri uri = user.getPhotoUrl();
                    saveImageUriString(getBaseContext(),uri.toString());
                    Intent intent = new Intent(LoginActivity.this,MainEntry.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    // User is signed in
                    Log.d("Tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {


                    // User is signed out
                    Log.d("Tag", "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        };

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);

        setupGoogleSignIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    /* Sign in with Google plus when user clicks "Sign in with Google" textView (button)
    */
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Log.d("Tag", "Sign in failed "+ result.getStatus());
                // [END_EXCLUDE]
                mAuthProgressDialog.dismiss();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Tag", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Tag", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Tag", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mAuthProgressDialog.dismiss();
                        }
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });

    }

    public void onCheckBoxClicked(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.USER_STATUS, isChecked);
        editor.commit();
    }
}
