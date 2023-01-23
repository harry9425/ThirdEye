package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.room.EntityDeletionOrUpdateAdapter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
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
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class bookadd extends AppCompatActivity  implements LocationListener{

    private EditText person, cityname, addressname;
    private String name, address, city,coor;
    Switch sw;
    ProgressDialog progressDialog,progressDialog2,progressDialog3;
    int allow = 1;
    private DatabaseReference mDatabase,mDatabase2;
    private static final int PERMISSION_CODE = 101;
    LocationManager locationManager;
    boolean isGpsLocation;
    Location loc;
    TextView acc;
    String[] permissions_all = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    boolean isNetworkLocation;
    Button b;
    public void usegps(View view) {
        Toast.makeText(this, "PLEASE WAIT WHILE APP FETCHES YOUR LOCATION", Toast.LENGTH_LONG).show();
        progressDialog2 = new ProgressDialog(bookadd.this);
        progressDialog2.setMessage("FETCHING YOUR LOCATION");
        progressDialog2.show();
        getlocation();
    }

    public void savedata(View view) {
        name = person.getText().toString().toLowerCase().trim();
        address = addressname.getText().toString().toLowerCase().trim();
        city = cityname.getText().toString().toLowerCase().trim();
        if (!address.contains(city)) {
            address += " " + city;
        }
        if (name.isEmpty() || address.isEmpty() || city.isEmpty()) {

            if (city.isEmpty()) {
                cityname.setError("empty field");
                cityname.requestFocus();
            }
            if (name.isEmpty()) {
                person.setError("empty field");
                person.requestFocus();
            }
            if (address.isEmpty()) {
                addressname.setError("empty field");
                addressname.requestFocus();
            }
            Toast.makeText(this, "PLEASE ENTER ABOVE FIELDS", Toast.LENGTH_LONG).show();
        } else {
            progressDialog3 = new ProgressDialog(bookadd.this);
            progressDialog3.setMessage("CHECKING AVAILABILITY OF NAME");
            progressDialog3.show();
            if (allow == 1)
                checkifanyexistson();
            else
                checkifanyexistsoff();
        }
    }

    private void checkifanyexistsoff() {
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("private").child(LOGIN.name+LOGIN.password+"data");
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(name)) {
                    progressDialog3.dismiss();
                    DialogFragment dialog = new bookadd.MyDialogFragment();
                    dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    Toast.makeText(bookadd.this, "Name already present try to store with diff one", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog3.dismiss();
                    progressDialog.show();
                    addoffdata();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                progressDialog3.dismiss();
                Toast.makeText(bookadd.this, "ERROR:INTERNET IS LOW", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addnodata() {
        mDatabase.child("ADDRESS BOOK").child("public").child(name).setValue(address+coor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(bookadd.this, "SYNCED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(bookadd.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(bookadd.this,book.class);
                startActivity(i);
            }
        },2000);
    }

    private void addoffdata() {
        mDatabase.child("ADDRESS BOOK").child("private").child(LOGIN.name+LOGIN.password+"data").child(name).setValue(address+coor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(bookadd.this, "SYNCED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(bookadd.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(bookadd.this,book.class);
                startActivity(i);
                finish();
            }
        },2000);
    }
    private void checkifanyexistson()
    {
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("public");
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(name)) {
                    progressDialog3.dismiss();
                    DialogFragment dialog = new bookadd.MyDialogFragment();
                    dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    Toast.makeText(bookadd.this, "Name already present try to store with diff one", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog3.dismiss();
                    progressDialog.show();
                    addnodata();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                progressDialog3.dismiss();
                Toast.makeText(bookadd.this, "ERROR:INTERNET IS LOW", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookadd);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        b=(Button) findViewById(R.id.savedata);
        acc=(TextView) findViewById(R.id.accuracy);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(bookadd.this);
        progressDialog.setMessage("SAVING DATA PUBLICLY");
        person = (EditText) findViewById(R.id.name);
        cityname = (EditText) findViewById(R.id.cityname);
        addressname = (EditText) findViewById(R.id.addressname);
        sw = (Switch) findViewById(R.id.onoff);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allow = 1;
                    b.setText("SAVE DATA ON SERVER PUBLICLY");
                    progressDialog.setMessage("SAVING DATA PUBLICLY");

                } else {
                    allow = 0;
                    b.setText("SAVE DATA ON SERVER PRIVATELY");
                    progressDialog.setMessage("SAVING DATA PRIVATELY");
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
            int result= ContextCompat.checkSelfPermission(bookadd.this,permissions_all[i]);
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
        ActivityCompat.requestPermissions(bookadd.this,permissions_all,PERMISSION_CODE);
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
        AlertDialog.Builder al=new AlertDialog.Builder(bookadd.this);
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10, (LocationListener) bookadd.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else if(isNetworkLocation)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10, (LocationListener) bookadd.this);
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
            progressDialog2.dismiss();
            Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUi(Location loc) {
        if (loc.getLatitude() == 0 && loc.getLongitude() == 0) {
            getdevicelocation();
        } else {
            progressDialog2.dismiss();
            Geocoder geocoder = new Geocoder(bookadd.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                city=addresses.get(0).getLocality();
                cityname.setText(city,TextView.BufferType.EDITABLE);
                addressname.setText(address, TextView.BufferType.EDITABLE);
                coor="\n\nCOORDINATES\n\n"+loc.getLatitude()+","+loc.getLongitude();
                double rounded1 = Math.round(loc.getAccuracy() * 10) / 10.0;
                if(rounded1<51)
                acc.setText(rounded1+30+"%");
                else if(rounded1<70)
                    acc.setText(rounded1+20+"%");
                else if(rounded1<80)
                    acc.setText(rounded1+10+"%");
                else
                    acc.setText(rounded1+"%");
                Toast.makeText(this, "LOCATION FETCHED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onLocationChanged(Location location) {

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

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ERROR FROM THIRD EYE");
            builder.setMessage("Name is already present in database try to write different name or delete previous one");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            return builder.create();
        }
    }
}
