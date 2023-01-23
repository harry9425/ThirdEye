
package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LOGIN extends AppCompatActivity {

    public static String name, password;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    Checkable checkable;
    File filet;
    EditText username, passkey;
    private AdView mAdView;
    String remdata,remname,rempass;
    TextView errortext;
    private InterstitialAd mInterstitialAd;

    public void openstore(View view) {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void openmail(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, "Found a bug in your app THIRD EYE");
        intent.setData(Uri.parse("mailto:hitanshagrawal@gmail.com")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }

    public void confirm(View view) {
        Intent intent34 = new Intent(LOGIN.this, forgetpass.class);
        startActivity(intent34);
    }

    public void checkdatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user:"+name).child("login");
        progressDialog = new ProgressDialog(LOGIN.this);
        progressDialog.setMessage("CHECKING CREDENTIALS");
        progressDialog.show();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("login"+name+password)) {
                    progressDialog.dismiss();
                    if (checkable.isChecked()) {
                        remember();
                    }
                    errortext.setText(" ");
                    Toast.makeText(LOGIN.this, "LOGGED IN SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LOGIN.this, chooser.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    errortext.setText("*WRONG USERID/PASSWORD.");
                    Toast.makeText(LOGIN.this, "WRONG ID/PASSWORD", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(LOGIN.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void login(View view)
    {
        name = username.getText().toString().toLowerCase();
        password = passkey.getText().toString();
        if (name.isEmpty() && password.isEmpty()) {
            username.setError("Field can't be left empty");
            username.requestFocus();
            passkey.setError("Field can't be left empty");
            passkey.requestFocus();
            Toast.makeText(this, "PLEASE ENTER ABOVE FIELDS", Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty() || password.isEmpty()) {
            if (name.isEmpty()) {
                username.setError("Field can't be left empty");
                username.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR USERNAME", Toast.LENGTH_SHORT).show();
            }
            if (password.isEmpty()) {
                passkey.setError("Field can't be left empty");
                passkey.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(name.startsWith(" ")||name.endsWith(" "))
                name= name.trim();
            if(password.startsWith(" ")||password.endsWith(" "))
                password= password.trim();
            checkdatabase();
        }
    }

    public void signup(View view)
    {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(this, signup.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l_o_g_i_n);
        errortext=(TextView) findViewById(R.id.error);
        checkable = (CheckBox) findViewById(R.id.remember);
        username = (EditText) findViewById(R.id.username);
        passkey = (EditText) findViewById(R.id.passkey);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        File dir = getFilesDir();
        filet = new File(dir, "credentials remember.txt");
        if (filet.exists()) {
            getremembered();
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
                Intent intent = new Intent(LOGIN.this, signup.class);
                startActivity(intent);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
    public void remember()
    {
        File file = null;
        String name1 = name+"&"+password;
        FileOutputStream fileOutputStream = null;
        try {
            file = getFilesDir();
            fileOutputStream = openFileOutput( "credentials remember.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(name1.getBytes());
            return;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Toast.makeText(this, "UNABLE TO REMEMBER", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void getremembered()
    {
        try {
            FileInputStream fileInputStream = openFileInput("credentials remember.txt");
            int read = -1;
            StringBuffer buffer = new StringBuffer();
            while ((read = fileInputStream.read()) != -1)
            {
                buffer.append((char) read);
            }
            String namet = buffer.substring(0);
            remdata=namet;
            if(!remdata.isEmpty())
            {
                String[] rem= remdata.split("&");
                remname=rem[0];
                rempass=rem[1];
                username.setText(rem[0].trim(), TextView.BufferType.EDITABLE);
                passkey.setText(rem[1].trim(), TextView.BufferType.EDITABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

