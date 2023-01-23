package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog progressDialog;
    Float d1=0f,d2=0f;
    String data2="not ava.";
    private DatabaseReference mDatabase2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        progressDialog = new ProgressDialog(MapsActivity2.this);
        progressDialog.setMessage("FINDING YOUR VEHICLE");
        progressDialog.show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkdatabase();
    }

    public void checkdatabase() {
            mDatabase2 = FirebaseDatabase.getInstance().getReference().child("parkingdata").child("user:"+LOGIN.name).child("coordinates");
            mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(MapsActivity2.this, "GETTING DATA FROM SERVER", Toast.LENGTH_LONG).show();
                        String data= snapshot.getValue().toString();
                        String cor[]=data.split(",");
                        d1=Float.parseFloat(cor[0]);
                        d2=Float.parseFloat(cor[1]);
                    onMapReady(mMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MapsActivity2.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();

                }
            });
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("parkingdata").child("user:"+LOGIN.name).child("altitude");
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(MapsActivity2.this, "GETTING DATA FROM SERVER", Toast.LENGTH_LONG).show();
                data2= snapshot.getValue().toString();
                progressDialog.dismiss();
                onMapReady(mMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity2.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();

            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(d1, d2);
        mMap.addMarker(new MarkerOptions().position(sydney).snippet("Lat:"+d1+" Lng:"+d2+" Alt:"+data2).title(LOGIN.name.toUpperCase()+"'s VEHICLE")).showInfoWindow();
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(18);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
