package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class savemyvehicle extends AppCompatActivity implements LocationListener {

    private DatabaseReference mDatabase;
    private RequestQueue requestQueue;
    private static final int PERMISSION_CODE = 101;
    LocationManager locationManager;
    boolean isGpsLocation;
    Button getlocation;
    Location loc;
    TextView coor,al;
    int allowtochange=1,whattodo=0;
    boolean isNetworkLocation;
    ProgressDialog progressDialog;
    String[] permissions_all = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savemyvehicle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        splashscreen.please2=1;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        coor=(TextView) findViewById(R.id.parkinglat);
        al=(TextView) findViewById(R.id.parkingAL);
        progressDialog = new ProgressDialog(savemyvehicle.this);
        progressDialog.setMessage("FETCHING LOCATION...\n\nIf dialog box keeps on spinning for too long, then press the start sending button once more\n or check your connections");
        getlocation = findViewById(R.id.yoyoyoyoy);
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowtochange==1) {
                    whattodo=1;
                    progressDialog.show();
                    getlocation();
                }
                else {
                    Toast.makeText(savemyvehicle.this, "CAN'T SAVE THE SAME POSITION.\nWAIT TILL THE APP RECEIVE LOCATION UPDATE\nOR TRY TO RESTART THE GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void adddataparking(int height,double lat,double lng)
    {
        if(splashscreen.please2==1) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("parkingdata").child("user:" + LOGIN.name).child("coordinates").setValue(lat + "," + lng).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                        Toast.makeText(savemyvehicle.this, "INTERNET IS LOW", Toast.LENGTH_LONG).show();
                    }
                }
            });
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("parkingdata").child("user:" + LOGIN.name).child("altitude").setValue(height).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(savemyvehicle.this, LOGIN.name.toUpperCase() + "DATA HAS BEEN SAVED", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(savemyvehicle.this, "INTERNET IS LOW", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

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
            int result= ContextCompat.checkSelfPermission(savemyvehicle.this,permissions_all[i]);
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
        ActivityCompat.requestPermissions(savemyvehicle.this,permissions_all,PERMISSION_CODE);
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
        AlertDialog.Builder al=new AlertDialog.Builder(savemyvehicle.this);
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10,savemyvehicle.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else if(isNetworkLocation)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10,savemyvehicle.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Press save button once again.", Toast.LENGTH_SHORT).show();
            }

        }
        catch (SecurityException e)
        {
            Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUi(Location loc) {
        if(loc.getLatitude()==0 && loc.getLongitude()==0){
            getdevicelocation();

        }
        else{
            progressDialog.dismiss();
            coor.setText(("Latitude: "+loc.getLatitude()+"\nLongitude: "+loc.getLongitude()));
            double altitude= loc.getAltitude();
            int roundoffal= (int) Math.round(altitude);
             al.setText("Height from the sea level:\n"+roundoffal+" meters");
             adddataparking(roundoffal,loc.getLatitude(),loc.getLongitude());
            getlocation.setText("WAIT");
            allowtochange=0;
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        allowtochange=1;
        getlocation.setText("SAVE");
        DialogFragment dialog = new savemyvehicle.MyDialogFragment();
        progressDialog.dismiss();
        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        if(whattodo==1) {
            AlertDialog.Builder al = new AlertDialog.Builder(savemyvehicle.this);
            al.setTitle("ALERT FROM THIRD EYE");
            al.setMessage("If you want to go back or access other features of the app you must close the app and restart it, and for doing so press the yes button.");
            al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), splashscreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
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
        else {
            Intent intent = new Intent(savemyvehicle.this, parkingstart.class);
            startActivity(intent);
        }
    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("LOCATION UPDATE AVAILABLE !!");
            builder.setMessage("You can now update your vehicle position by pressing the save button");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            return builder.create();
        }
    }

}
