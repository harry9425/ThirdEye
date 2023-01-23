package com.harrytheboss.wtfishappening;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.harrytheboss.wtfishappening.app.CHANNEL_1_ID;

public class chooser extends AppCompatActivity {

    private InterstitialAd mInterstitialAd,mInterstitialAd2,mInterstitialAd3;

    public void gpsproblemsstart(View view)
    {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(this, gpsproblems.class);
            startActivity(intent);
        }
    }
    public void gpsproblemsstart2(View view)
    {
        if (mInterstitialAd2.isLoaded()) {
            mInterstitialAd2.show();
        } else {
            Intent intent = new Intent(this, parkingstart.class);
            startActivity(intent);
        }
    }

    public void gpsproblemsstart3(View view)
    {
        if (mInterstitialAd3.isLoaded()) {
            mInterstitialAd3.show();
        } else {
            Intent intent = new Intent(this, book.class);
            startActivity(intent);
        }
    }

    private void gpsproblemsstart4() {
        File file = null;
        String name1 = LOGIN.name+"&"+" ";
        FileOutputStream fileOutputStream = null;
        try {
            file = getFilesDir();
            fileOutputStream = openFileOutput( "credentials remember.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(name1.getBytes());
            Intent intent = new Intent(this, LOGIN.class);
            startActivity(intent);
            finish();
            return;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Toast.makeText(this, "UNABLE TO SIGNED OUT", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    public void proceed(View view) {
        AlertDialog.Builder al=new AlertDialog.Builder(chooser.this);
        al.setTitle("MESSAGE FROM THIRD EYE");
        al.setMessage("WANT TO SIGN OUT ?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gpsproblemsstart4();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
                Intent intent = new Intent(chooser.this, gpsproblems.class);
                startActivity(intent);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        mInterstitialAd2 = new InterstitialAd(this);
        mInterstitialAd2.setAdUnitId("ca-app-pub-9227296073858132/9096435869");
        mInterstitialAd2.loadAd(new AdRequest.Builder().build());
        mInterstitialAd2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                Intent intent2 = new Intent(chooser.this, parkingstart.class);
                startActivity(intent2);
                mInterstitialAd2.loadAd(new AdRequest.Builder().build());
            }

        });
        mInterstitialAd3 = new InterstitialAd(this);
        mInterstitialAd3.setAdUnitId("ca-app-pub-9227296073858132/9096435869");
        mInterstitialAd3.loadAd(new AdRequest.Builder().build());
        mInterstitialAd3.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                Intent intent3 = new Intent(chooser.this, book.class);
                startActivity(intent3);
                mInterstitialAd3.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("REQUEST FROM THIRD EYE!!");
            builder.setMessage("PLEASE RESTART THE APP TO ACCESS GPS TRACKING AGAIN.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            return builder.create();
        }
    }

}
