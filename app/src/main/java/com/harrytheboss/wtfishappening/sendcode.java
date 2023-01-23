package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sendcode extends AppCompatActivity {

    ProgressDialog progressDialog;
    int code,allowtochange=0;
    TextView text;
    Button ionii;
    private DatabaseReference mDatabase;
   public void generate()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("codes").child(LOGIN.name);
        mDatabase.removeValue();
        progressDialog.show();
        Random rand = new Random();
        code= rand.nextInt(899)+100;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("codes").child(LOGIN.name).child(LOGIN.name+"@"+code).setValue(LOGIN.name+"@"+code).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    TextView display=(TextView) findViewById(R.id.codedisplay);
                    display.setText(LOGIN.name+"@"+code);
                    Toast.makeText(sendcode.this, "CODE GENERATED SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    ionii.setText("Share code by whatsapp");
                    text.setText("*Press to share code with your friend's");
                    allowtochange=1;
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(sendcode.this, "INTERNET IS LOW", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteall(View view)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("codes").child(LOGIN.name);
        mDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(sendcode.this, "DEACTIVATED PREVIOUS CODE SUCCESSFULLY", Toast.LENGTH_LONG).show();
                Intent i=new Intent(sendcode.this,gpsproblems.class);
                startActivity(i);
            }
        });
    }

    private void sendwhatsapp()
    {
        allowtochange=0;
        ionii.setText("GENERATE NEW CODE");
        text.setText("*Press to generate a random code");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "MESSAGE BY THIRD EYE APP :\n\nYour friend "+ LOGIN.name+ " has sended you this code:\n\ncode=> "+LOGIN.name+"@"+code+
                                                      "\n\npaste the above code in the link given below to track him.\n\nlink for tracking is : thethirdeyeapp.trackmyfriend.code.com\n\n" +
                                                      "if you don't have third eye app installed then \npress the link below to download it:\nhttps://play.google.com/store/apps/details?id=com.harrytheboss.wtfishappening");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendcode);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = new ProgressDialog(sendcode.this);
        progressDialog.setMessage("Generating Code");
        text=(TextView) findViewById(R.id.textView51);
        text.setText("*Press to generate a random code");
        ionii=(Button) findViewById(R.id.ioniii);
        ionii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowtochange==0) {
                    progressDialog.show();
                    generate();
                }
                if(allowtochange==1)
                {
                    sendwhatsapp();
                }
            }
        });
    }
}
