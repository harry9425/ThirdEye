package com.harrytheboss.wtfishappening;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class moreinfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreinfo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
