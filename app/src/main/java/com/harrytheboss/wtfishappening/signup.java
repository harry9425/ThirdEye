package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
import java.util.ArrayList;

public class signup extends AppCompatActivity {

    public static String name,password,password2,emailid;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    CheckBox allow;
    private AdView mAdView;
    public static String userid;

    public void moreinfo(View view)
    {
        Intent intent = new Intent(signup.this, moreinfo.class);
        startActivity(intent);
    }


    public void login(View view)
    {
        EditText user = (EditText) findViewById(R.id.name);
        EditText passkey = (EditText) findViewById(R.id.pass);
        EditText passkey2 = (EditText) findViewById(R.id.pass2);
        EditText email = (EditText) findViewById(R.id.userid);
        name = user.getText().toString().toLowerCase();
        password = passkey.getText().toString();
        password2 = passkey2.getText().toString();
        emailid = email.getText().toString();
        if (name.isEmpty() && password.isEmpty() &&password2.isEmpty() && emailid.isEmpty()) {
            user.setError("empty field");
            user.requestFocus();
            passkey.setError("empty field");
            passkey.requestFocus();
            passkey2.setError("empty field");
            passkey2.requestFocus();
            email.setError("empty field");
            email.requestFocus();
            Toast.makeText(this, "PLEASE ENTER ABOVE FIELDS", Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty() || password.isEmpty()|| password2.isEmpty()|| emailid.isEmpty()) {
            if (name.isEmpty()) {
                user.setError("empty field");
                user.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR USERNAME", Toast.LENGTH_SHORT).show();
            }

            if (password.isEmpty()) {
                passkey.setError("empty field");
                passkey.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
            }
            if (password2.isEmpty()) {
                passkey2.setError("empty field");
                passkey2.requestFocus();
                Toast.makeText(this, "PLEASE CONFIRM YOUR PASSWORD", Toast.LENGTH_SHORT).show();
            }
            if (emailid.isEmpty() ) {
                email.setError("empty field");
                email.requestFocus();
                Toast.makeText(this, "PLEASE ENTER YOUR PHONE NO.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(password.length()>=6 && password2.length()>=6){

            if(name.startsWith(" ")||name.endsWith(" "))
                name= name.trim();
            if(password.startsWith(" ")||password.endsWith(" "))
                password= password.trim();
            if(password2.startsWith(" ")||password2.endsWith(" "))
                password2= password2.trim();

            if(password.equals(password2)) {
                if (emailid.length() != 10) {
                    if(emailid.length()<10)
                    {
                        email.setError("Mobile no. must contains 10 digits");
                        email.requestFocus();
                        Toast.makeText(this, "Mobile no. must contains 10 digits", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        email.setError("Invalid phone no.\nTry without writing country code");
                        email.requestFocus();
                        Toast.makeText(this, "Try not to write country code", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (allow.isChecked()) {
                        checkifanyexists();
                    }
                    else
                        Toast.makeText(this, "CHECK THE CHECKBOX", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                passkey.setError("Password don't match");
                passkey.requestFocus();
                passkey2.setError("Password don't match");
                passkey2.requestFocus();
                Toast.makeText(this, "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
            }
            }
        else
        {
            if(password.length()<6) {
                passkey.setError("Password length should be 6 character long or more");
                passkey.requestFocus();
            }
            if(password2.length()<6) {
                passkey2.setError("Password length should be 6 character long or more");
                passkey2.requestFocus();
            }
            Toast.makeText(this, "PASSWORD MUST BE 6 CHARACTER", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkifanyexists()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(signup.this);
        progressDialog.setMessage("CHECKING CREDENTIALS");
        progressDialog.show();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("user:"+name)) {
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "USERNAME NOT AVAILABLE", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.dismiss();
                    save();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(signup.this, "ERROR:INTERNET IS LOW", Toast.LENGTH_LONG).show();

            }
        });
    }

   public void save()
   {
      Intent intent = new Intent(getApplicationContext(), otpverification.class);
        otpverification.phoneNo="+91"+emailid;
        startActivity(intent);
   }

   void adddata() {
           progressDialog = new ProgressDialog(signup.this);
           progressDialog.setMessage("CREATING ACCOUNT");
           progressDialog.show();
           mDatabase.child("user:" + name).child("login").child("login" + name + password).setValue( name + password).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()) {
                       Toast.makeText(signup.this, "CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                      addnodata();

                   } else {
                       progressDialog.dismiss();
                       Toast.makeText(signup.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                   }
               }
           });
   }
   private void addnodata()
   {
       mDatabase.child("user:" + name).child("phone").setValue(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   addnodata2();

               } else {
                   progressDialog.dismiss();
                   Toast.makeText(signup.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
               }
           }
       });
   }
    private void addnodata2()
    {
        mDatabase.child("user:" + name).child("address").setValue("NO PREVIOUS RECORD FOUND").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addnodata3();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addnodata3()
    {
        mDatabase.child("user:" + name).child("coordinates").setValue("20.5937,78.9629").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signup.this, LOGIN.class);
                    otpverification.access=0;
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
         mDatabase = FirebaseDatabase.getInstance().getReference();
         allow=(CheckBox) findViewById(R.id.allowcheckbox);
         if(otpverification.access==1)
         {
             adddata();
         }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("9227296073858132/5533746756");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


         }
}
