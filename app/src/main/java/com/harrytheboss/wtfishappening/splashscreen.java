package com.harrytheboss.wtfishappening;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class splashscreen extends AppCompatActivity {

public static int please=0,please2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        VideoView video = (VideoView) findViewById(R.id.videoView);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.intro);
        video.seekTo(500);
        video.start();
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            this.finishAffinity();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent2=new Intent(splashscreen.this,LOGIN.class);
                    startActivity(intent2);
                    finish();
                }
            },3700);
        }
    }
}
