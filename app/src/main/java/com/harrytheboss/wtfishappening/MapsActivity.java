package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import static com.harrytheboss.wtfishappening.app.CHANNEL_1_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    float d1=0f,d2=0f;
    int a=0;
    public static int plzwork=1;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase2,mDatabase3;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        notificationManager = NotificationManagerCompat.from(this);
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Please wait till the map loads");
        progressDialog.show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        if(plzwork==1) {
            DialogFragment dialog = new MapsActivity.MyDialogFragment();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
            getdata();
        }
        else {
            DialogFragment dialog = new MapsActivity.MyDialogFragment2();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
              sosreceived();
        }
    }
      private void getdata()
       {

           mDatabase2 = FirebaseDatabase.getInstance().getReference().child("user:"+LOGIN.name).child("coordinates");
           mDatabase2.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   Toast.makeText(MapsActivity.this, "GETTING DATA FROM SERVER", Toast.LENGTH_LONG).show();
                   String data=snapshot.getValue().toString();
                   String cor[]=data.split(",");
                   d1=Float.parseFloat(cor[0]);
                   d2=Float.parseFloat(cor[1]);
                   sendOnChannel1(LOGIN.name.toUpperCase()+"'S LOCATION UPDATE.");
                   a++;
                   Toast.makeText(MapsActivity.this, "Lat= "+d1+"\nLong= "+d2, Toast.LENGTH_LONG).show();
                   progressDialog.dismiss();
                   onMapReady(mMap);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(MapsActivity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
               }
           });
       }

    private void sosreceived()
    {
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("user:"+LOGIN.name);
        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("sos")) {
                    Toast.makeText(MapsActivity.this, "SOS RECEIVED", Toast.LENGTH_LONG).show();
                    DialogFragment dialog = new getdatafromserver.MyDialogFragment();
                    dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    mDatabase3.child("sos").removeValue();
                    sendOnChannel1("SOS RECEIVED: IT'S AN EMERGENCY, ACT QUICKLY!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapsActivity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
        getdata();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(d1,d2);
        mMap.addMarker(new MarkerOptions().position(sydney).snippet("POSITION NO."+a).title(LOGIN.name.toUpperCase()+"'s LOCATION")).showInfoWindow();
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(14);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public  static class MyDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ALERT FROM THIRD EYE");
            builder.setMessage("\nDuring live mode the app will not receive any sos if sent by other user.\n\n" +
                    "To see if any sos is received then go back to normal view and after seeing sos if any is present, you can reopen the live mode.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            return builder.create();
        }
    }
    public  static class MyDialogFragment2 extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ALERT FROM THIRD EYE");
            builder.setMessage("\nDuring tracking the app will not receive any updates regarding sos if sent by other user.\n\n" +
                    "To see if any sos is received then go back to previous page and open tracking again.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            return builder.create();
        }
    }

    public void sendOnChannel1(String mess) {
        String title ="THIRD EYE LIVE MODE:";
        String message = mess;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notiname)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification);
    }

}
