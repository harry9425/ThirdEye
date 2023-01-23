package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgetpass extends AppCompatActivity {

    EditText id;
    public static String nameofuser;
    public static String phoneofuser;
    private DatabaseReference mDatabase,mDatabase2;
    ProgressDialog progressDialog;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);
        id=(EditText) findViewById(R.id.username);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-9227296073858132/5533746756");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
                Intent intent = new Intent(forgetpass.this, resetallow.class);
                startActivity(intent);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }
    public void hello(View view)
    {
        nameofuser=id.getText().toString().toLowerCase();
        if(nameofuser.isEmpty())
        {
            id.setError("Field can't be left empty");
            id.requestFocus();
        }
        else {
            if(nameofuser.startsWith(" ")||nameofuser.endsWith(" "))
                nameofuser= nameofuser.trim();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            progressDialog = new ProgressDialog(forgetpass.this);
            progressDialog.setMessage("FINDING YOUR ACCOUNT");
            progressDialog.show();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("user:" + nameofuser)) {
                        Toast.makeText(forgetpass.this, "ACCOUNT FOUND", Toast.LENGTH_LONG).show();
                        getusernumber();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(forgetpass.this, "ACCOUNT DOES NOT EXISTS", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(forgetpass.this, "ERROR:INTERNET IS LOW", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private void getusernumber()
    {
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("user:"+nameofuser).child("phone");
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                Toast.makeText(forgetpass.this, "GETTING DATA FROM SERVER", Toast.LENGTH_LONG).show();
                phoneofuser="+91"+snapshot.getValue().toString();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Intent intent = new Intent(forgetpass.this, resetallow.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(forgetpass.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
}
