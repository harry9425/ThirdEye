package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.harrytheboss.wtfishappening.app.CHANNEL_1_ID;

public class parkingstart extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;
    Intent i;
    private AdView mAdView;
    ProgressDialog progressDialog;
    int allowtorun=0;
    private InterstitialAd mInterstitialAd;
    public void confirm()
    {
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!mKeyguardManager.isKeyguardSecure()) {
            Toast.makeText(this, "CAN'T OPEN THE PAGE,TO OPEN THE PAGE FIRST \n GO TO SETTING>SECURITY AND ADD LOCK TO YOUR DEVICE", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {

                startActivity(i);

            } else {
                Toast.makeText(this, "AUTHENTICATION CANCELLED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gotocurrent(View view)
    {
        i=new Intent(this,savemyvehicle.class);
        confirm();
    }
    public void showtocurrent(View view)
    {
        if(allowtorun==1) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                i = new Intent(this, MapsActivity2.class);
                confirm();
            }
        }
        else {
            Toast.makeText(parkingstart.this, "Please wait while syncing data with server finishes..", Toast.LENGTH_LONG).show();
        }
    }


    public void checkup()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("parkingdata").child("user:"+LOGIN.name).child("coordinates").setValue(0+","+0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                   allowtorun=1;
                }
                else {
                    allowtorun=0;
                    Toast.makeText(parkingstart.this, "INTERNET IS LOW, COULD'NT SYNC", Toast.LENGTH_LONG).show();
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("parkingdata").child("user:"+LOGIN.name).child("altitude").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.show();
                    allowtorun=1;
                    Toast.makeText(parkingstart.this, LOGIN.name.toUpperCase()+"DATA HAS BEEN SYNCED", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.show();
                    allowtorun=0;
                    Toast.makeText(parkingstart.this, "INTERNET IS LOW, COULD'NT SYNC", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void checkdatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("parkingdata").child("user:"+LOGIN.name);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("coordinates")) {
                        progressDialog.dismiss();
                        allowtorun=1;

                    } else {
                        Toast.makeText(parkingstart.this, "syncing....", Toast.LENGTH_LONG).show();
                          checkup();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(parkingstart.this, "INTERNET IS LOW, COULD'NT SYNC", Toast.LENGTH_LONG).show();
                }
            });
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingstart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        splashscreen.please2=0;
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-9227296073858132/5533746756");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        progressDialog = new ProgressDialog(parkingstart.this);
        progressDialog.setMessage("SYNCING WITH SERVER");
        progressDialog.show();
        checkdatabase();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9227296073858132/9096435869");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                i = new Intent(parkingstart.this, MapsActivity2.class);
                confirm();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }
}
