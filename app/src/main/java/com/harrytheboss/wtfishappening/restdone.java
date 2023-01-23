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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class restdone extends AppCompatActivity {
    private DatabaseReference mDatabase24;
    ProgressDialog progressDialog;
    String password,password2;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restdone);
        progressDialog = new ProgressDialog(restdone.this);
        progressDialog.setMessage("RESETTING PASSWORD");

        mDatabase24= FirebaseDatabase.getInstance().getReference();
        Toast.makeText(this, "WELCOME "+forgetpass.nameofuser.toUpperCase(), Toast.LENGTH_SHORT).show();
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
                Intent intent34 = new Intent(restdone.this, LOGIN.class);
                startActivity(intent34);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        }
    public void proceed(View view)
    {
        progressDialog.show();
        EditText pass1=(EditText) findViewById(R.id.liker);
        EditText pass2=(EditText) findViewById(R.id.bsdl);
        password=pass1.getText().toString();
        password2=pass2.getText().toString();
        if (password.isEmpty() &&password2.isEmpty()) {
            pass1.setError("empty field");
            pass1.requestFocus();
            pass2.setError("empty field");
            pass2.requestFocus();
            Toast.makeText(this, "PLEASE ENTER ABOVE FIELDS", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()|| password2.isEmpty()) {

            if (password.isEmpty()) {
                pass1.setError("empty field");
                pass1.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
            }
            if (password2.isEmpty()) {
                pass2.setError("empty field");
                pass2.requestFocus();
                Toast.makeText(this, "PLEASE CONFIRM YOUR PASSWORD", Toast.LENGTH_SHORT).show();
            }
        }
        else if(password.length()>=6 && password2.length()>=6){
            if(password.startsWith(" ")||password.endsWith(" "))
                password= password.trim();
            if(password2.startsWith(" ")||password2.endsWith(" "))
                password2= password2.trim();
            if(password.equals(password2)) {
                addchanges();
            }
            else {
                pass2.setError("Password don't match");
                pass2.requestFocus();
                pass1.setError("Password don't match");
                pass1.requestFocus();
                Toast.makeText(this, "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            pass1.setError("Password length should be 6 character long or more");
            pass1.requestFocus();
            Toast.makeText(this, "PASSWORD MUST BE 6 CHARACTER", Toast.LENGTH_SHORT).show();
        }
    }

    public void addchanges()
    {
        mDatabase24.child("user:"+ forgetpass.nameofuser).child("login").removeValue();
        mDatabase24.child("user:"+ forgetpass.nameofuser).child("login").child("login"+forgetpass.nameofuser+password).setValue(forgetpass.nameofuser+password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(restdone.this, "PASSWORD CHANGED", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Intent intent34 = new Intent(restdone.this, LOGIN.class);
                        startActivity(intent34);
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(restdone.this, "ERROR OCCURRED", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    }

