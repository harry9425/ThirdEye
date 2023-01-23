package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private DatabaseReference mDatabase;
    private RequestQueue requestQueue;
    private static final int PERMISSION_CODE = 101;
    LocationManager locationManager;
    boolean isGpsLocation;
    String address;
    boolean isNetworkLocation;
    TextView loactiontext;
    TextView cortotext;
    private InterstitialAd mInterstitialAd;
    Button getlocation;
    Location loc;
    Switch sw;
    private AdView mAdView;
    ScrollView sc;
    public static VideoView video;
    ProgressDialog progressDialog;
    String[] permissions_all = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Intent intent = new Intent(MainActivity.this, getdatafromserver.class);
                startActivity(intent);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        DialogFragment dialog = new MainActivity.MyDialogFragment();
        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        video = (VideoView) findViewById(R.id.videogoon);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.appvideo);
        video.seekTo(500);
        MediaController mediaController = new MediaController(this);
        video.setMediaController(mediaController);
        mediaController.setAnchorView(video);
        sw = (Switch) findViewById(R.id.switch2);
        sc = (ScrollView) findViewById(R.id.scrollView3);
        video.setVisibility(View.INVISIBLE);
        sc.setVisibility(View.VISIBLE);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    video.start();
                    sc.setVisibility(View.INVISIBLE);
                    video.setVisibility(View.VISIBLE);
                } else {
                    video.pause();
                    ScrollView sc = (ScrollView) findViewById(R.id.scrollView3);
                    sc.setVisibility(View.VISIBLE);
                    video.setVisibility(View.INVISIBLE);
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("FETCHING LOCATION...\n\nIf dialog box keeps on spinning for too long, then press the start sending button once more\n or check your connections");
        loactiontext = findViewById(R.id.locationtext2);
        getlocation = findViewById(R.id.getlocation);
        cortotext = findViewById(R.id.addressdisplay2);
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                getlocation();
            }
        });


    }

    public void search(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(this, getdatafromserver.class);
            Toast.makeText(this, "WELCOME", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }


    public void addchanges(String address,Double lat,Double lng)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user:"+LOGIN.name).child("address").setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, LOGIN.name, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "address not send!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user:"+LOGIN.name).child("coordinates").setValue(lat+","+lng).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "coordinates sended..", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "coordinates not send!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sos(View view)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user:"+LOGIN.name).child("sos").setValue("help").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, LOGIN.name.toUpperCase()+" SOS SENT", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "address not send!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

private void getlocation()
{
    if(Build.VERSION.SDK_INT>=23)
    {
     if(checkPermission())
     {
         getdevicelocation();
     }
     else
     {
         requestPermissions();
     }
    }
    else {
        getdevicelocation();
    }
}

   private boolean checkPermission()
    {

        for(int i=0;i<permissions_all.length;i++)
        {
            int result= ContextCompat.checkSelfPermission(MainActivity.this,permissions_all[i]);
            if(result== PackageManager.PERMISSION_GRANTED)
            {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

private void getdevicelocation()
    {
       locationManager=(LocationManager)getSystemService(Service.LOCATION_SERVICE);
       isGpsLocation=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
       isNetworkLocation=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
       if(!isNetworkLocation&&!isGpsLocation)
       {
           showSettingForLocation();
           getLastlocation();
       }
       else
       {
           getFinalLocation();
       }
    }

    private void getLastlocation() {
        if(locationManager!=null) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,false);
                Location location=locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(MainActivity.this,permissions_all,PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    getFinalLocation();
                }
                else{
                    Toast.makeText(this, "Permission Failed", Toast.LENGTH_SHORT).show();
              }
        }
    }

    private void showSettingForLocation() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        al.setTitle("Location Not Enabled!");
        al.setMessage("Enable Location ?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        al.show();
    }
    private void   getFinalLocation()
    {
        try {
            if(isGpsLocation)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10,MainActivity.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else if(isNetworkLocation)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10,MainActivity.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else
            {
                DialogFragment dialog = new MainActivity.MyDialogFragment2();
                dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                progressDialog.dismiss();
                Toast.makeText(this, "PRESS SEND LOCATION BUTTON ONCE MORE", Toast.LENGTH_SHORT).show();
            }
        }
        catch (SecurityException e)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUi(Location loc) {
        if(loc.getLatitude()==0 && loc.getLongitude()==0){
            getdevicelocation();
        }
        else{
            progressDialog.dismiss();
            TextView extra2=(TextView) findViewById(R.id.locationtex2);
            extra2.setText("COORDINATES:");
            loactiontext.setText("\nLATITUDE:"+loc.getLatitude()+"\n LONGITUDE: "+loc.getLongitude());
            Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                List<Address> addresses=geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                 address=addresses.get(0).getAddressLine(0);
                TextView extra=(TextView) findViewById(R.id.gogo2);
                extra.setText("ADDRESS:");
                cortotext.setText("\n"+address);
                addchanges(address,loc.getLatitude(),loc.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    updateUi(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ALERT FROM THIRD EYE");
            builder.setMessage("Please ensure to switch on gps/location for faster tracking.\n\nIf gps isn't on then the app will give/send your location with the help of your network provider " +
                    "and it can lead to slower results.\n\n" +
                    "Sometimes fetching location dialog keeps on spinning, to solve that just restart the app while keeping the gps on.\n"+
                    "\nIn case of any emergency, if you want to send an sos make sure that your mobile internet is on and sending sos can take few seconds so try not to close the app or gps after sending sos.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            return builder.create();
        }
    }
    public static class MyDialogFragment2 extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("MESSAGE FROM THIRD EYE");
            builder.setMessage("Press start sending button once again to start tracking");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            return builder.create();
        }
    }
}
