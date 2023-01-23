package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class typecode extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String codebyuser;
    ProgressDialog progressDialog;
    String[] details;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    public void checkcode(View view) {
        EditText codes = (EditText) findViewById(R.id.codewrite);
        codebyuser = codes.getText().toString();
        if (codebyuser.isEmpty()) {
            codes.setError("empty field");
            codes.requestFocus();
            Toast.makeText(typecode.this, "PLEASE ENTER THE CODE", Toast.LENGTH_LONG).show();
        }
        else {
            if(codebyuser.startsWith(" ")||codebyuser.endsWith(" "))
                codebyuser= codebyuser.trim();
            progressDialog.show();
            details = codebyuser.split("@");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("codes").child(details[0]);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(codebyuser)) {
                        progressDialog.dismiss();
                        Toast.makeText(typecode.this, "VERIFIED SUCCESSFULLY", Toast.LENGTH_LONG).show();
                        LOGIN.name=details[0];
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Intent i = new Intent(typecode.this, kyu.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(typecode.this, "WRONG CODE", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(typecode.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typecode);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = new ProgressDialog(typecode.this);
        progressDialog.setMessage("CHECKING CODE");

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
                Intent i = new Intent(typecode.this, kyu.class);
                startActivity(i);
                finish();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }
}
