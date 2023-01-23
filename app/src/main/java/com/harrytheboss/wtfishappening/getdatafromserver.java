package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.harrytheboss.wtfishappening.app.CHANNEL_1_ID;
import static com.harrytheboss.wtfishappening.app.CHANNEL_2_ID;

public class getdatafromserver extends AppCompatActivity {

    private DatabaseReference mDatabase,mDatabase2,mDatabase3;
    private AdView mAdView;
    private TextView displayData1,displayData2,displayData3,displayData4;
    String name=LOGIN.name,pass=LOGIN.password;
    public static String foi;
    String dataty;
    private NotificationManagerCompat notificationManager;

    public void openmaps(View view) {
       Intent intent = new Intent(this, MapsActivity.class);
        Toast.makeText(getdatafromserver.this, "WELCOME IN LIVE MODE", Toast.LENGTH_LONG).show();
        MapsActivity.plzwork=1;
        startActivity(intent);
    }

    private void displayval1() {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user:" + name).child("address");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getdatafromserver.this, "GETTING DATA FROM SERVER", Toast.LENGTH_LONG).show();
                 dataty = snapshot.getValue().toString();
                displayData1.setText(dataty + "\n");
                displayData2.setText("ADDRESS:\n");
                sosreceived();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getdatafromserver.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
            mDatabase2 = FirebaseDatabase.getInstance().getReference().child("user:"+name).child("coordinates");
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String data=snapshot.getValue().toString();
                    foi=data;
                    String[] cor=data.split(",");
                    displayData3.setText("COORDINATES:\n");
                    displayData4.setText(("LATITUDE:"+cor[0]+"\n"+"LONGITUDE:"+cor[1]));
                    sendOnChannel1("NEW LOCATION: "+dataty+"\nNEW COORDINATES: "+cor[0]+","+cor[1]);
                    sosreceived();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getdatafromserver.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void sosreceived()
    {
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("user:"+name);
        mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("sos")) {
                        Toast.makeText(getdatafromserver.this, "SOS RECEIVED", Toast.LENGTH_LONG).show();
                        DialogFragment dialog = new getdatafromserver.MyDialogFragment();
                        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                        mDatabase3.child("sos").removeValue();
                        sendOnChannel1("SOS RECEIVED: IT'S AN EMERGENCY, ACT QUICKLY!!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getdatafromserver.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getdatafromserver);
        MainActivity.video.seekTo(500);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-9227296073858132/5533746756");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        notificationManager = NotificationManagerCompat.from(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase2 = FirebaseDatabase.getInstance().getReference();
        mDatabase3 = FirebaseDatabase.getInstance().getReference();
         displayval1();

        displayData1=(TextView) findViewById(R.id.addressdisplay2);
        displayData2=(TextView) findViewById(R.id.gogo2);
        displayData3=(TextView) findViewById(R.id.locationtex2);
        displayData4=(TextView) findViewById(R.id.locationtext2);
        TextView namedisplay=(TextView) findViewById(R.id.trackname);
        namedisplay.setText( "Track "+name.toUpperCase());

    }

    public  static  class MyDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ALERT FROM THIRD EYE :\nRECEIVED SOS FROM "+LOGIN.name.toUpperCase());
            builder.setMessage("\nI NEED HELP!!!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            return builder.create();
        }
    }


    public void sendOnChannel1(String mess) {
        String title ="THIRD EYE";
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
