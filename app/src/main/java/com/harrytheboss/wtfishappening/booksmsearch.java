package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class booksmsearch extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String namets,nameos,result="",cityss;
    EditText nametosearched,citytosearch;
    TextView displaysms,sstext,countdis;
    ScrollView down;
    Switch ud;
    int count=0;
    ImageView logo;
    String[] parts;
    ProgressDialog progressDialog;
    CheckBox pub,pri,city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booksmsearch);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        nametosearched=(EditText) findViewById(R.id.smsname);
        citytosearch=(EditText) findViewById(R.id.smscity);
        displaysms=(TextView) findViewById(R.id.smsdisplay);
        countdis=(TextView) findViewById(R.id.countdisplay);
        sstext=(TextView) findViewById(R.id.sstext);
        down=(ScrollView) findViewById(R.id.scrollsms);
        sstext.setMovementMethod(new ScrollingMovementMethod());
        sstext.setHorizontallyScrolling(true);
        pub=(CheckBox) findViewById(R.id.checkpub);
        pri=(CheckBox) findViewById(R.id.checkpri);
        city=(CheckBox) findViewById(R.id.checkcity);
        logo=(ImageView) findViewById(R.id.googlelogo);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()){
                    logo.setVisibility(View.INVISIBLE);
                    citytosearch.setVisibility(View.VISIBLE);}
                else {
                    citytosearch.setVisibility(View.INVISIBLE);
                    logo.setVisibility(View.VISIBLE);
                }
            }
        });

        nametosearched.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(!nametosearched.getText().toString().trim().isEmpty()) {
                    sstext.setText(nametosearched.getText().toString().toUpperCase().trim());
                }
                else sstext.setText("XXXX");
            }
        });
        down.setScrollY(0);
        ud = (Switch) findViewById(R.id.updown);
        ud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    down.post(new Runnable() {
                        public void run() {
                            down.smoothScrollTo(down.getScrollY(), down.getTop());
                        }
                    });
                } else {
                    down.post(new Runnable() {
                        public void run() {
                            down.smoothScrollTo(down.getScrollY(), displaysms.getBottom());
                        }
                    });
                }
            }
        });
    }

    public void searchsms(View view) {
        result = "";
        count=0;
        progressDialog = new ProgressDialog(booksmsearch.this);
        progressDialog.setMessage("Searching databases");
        namets = nametosearched.getText().toString().toLowerCase().trim();
        cityss = citytosearch.getText().toString().toLowerCase().trim();
        if (namets.isEmpty()) {
            nametosearched.setError("Empty!");
            nametosearched.requestFocus();
        } else {
            if(city.isChecked()) {
                if (cityss.isEmpty()) {
                    citytosearch.setError("Empty!");
                    citytosearch.requestFocus();
                    return;
                }
            }
            else {
                cityss="";
            }
            progressDialog.show();
                if (pri.isChecked() && pub.isChecked()) {
                    off();
                    on();
                } else if (pri.isChecked()) {
                    off();
                } else if (pub.isChecked()) {
                    on();
                } else {
                    pri.setChecked(TRUE);
                    pub.setChecked(TRUE);
                    off();
                    on();
                    Toast.makeText(booksmsearch.this, "Checking in both domains", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void displays(View view)
    {
        if(count==1) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(("geo:0,0?q=" + parts[1])));
            if (intent.resolveActivity(getPackageManager()) != null) {
                Toast.makeText(this, "Address Found Opening GPS..", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(booksmsearch.this, "Enter name of person such that only one required result shows up, to open location in gps", Toast.LENGTH_SHORT).show();
            proceed();
        }
    }
    private void proceed() {
        AlertDialog.Builder al=new AlertDialog.Builder(booksmsearch.this);
        al.setTitle("CAN'T OPEN ADDRESS IN GPS WHEN MULTIPLE RECORDS ARE PRESENT");
        al.setMessage("Enter name of person such that only one required result shows up, to open location in gps.\nElse go back and write his/her full name to search it.\n\nWant to go back?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            Intent i=new Intent(booksmsearch.this,book.class);
            startActivity(i);
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

    private void on()
    {
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("public");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    nameos = ds.getKey();
                    if (nameos.contains(namets)) {
                        parts =ds.getValue().toString().split("\\n\\nCOORDINATES\\n\\n");
                        if(!result.contains(nameos + "\n\t\tAddress : " + parts[0])) {
                            if(city.isChecked()) {
                                if(parts[0].contains(cityss)) {
                                    count++;
                                    result += "\t\tPerson: " + count + " :\t" + nameos + "\n\t\tAddress : " + parts[0] + "\t\t\n\n";
                                }
                            }
                            else {
                                count++;
                                result += "\t\tPerson: " + count + " :\t" + nameos + "\n\t\tAddress : " + parts[0] + "\t\t\n\n";
                            }
                        }
                    }
                }
                if (result.equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(booksmsearch.this, "NO RECORD FOUNDED IN DOMAIN", Toast.LENGTH_LONG).show();
                    displaysms.setText("\n\n\t\tNO RECORDS FOUND IN THIRD EYE DATABASE");
                } else {
                    progressDialog.dismiss();
                    countdis.setText(count+". Records present");
                    displaysms.setText(result);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booksmsearch.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void off()
    {
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("private").child(LOGIN.name+LOGIN.password+"data");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    nameos = ds.getKey();
                    if (nameos.contains(namets)) {
                        parts =ds.getValue().toString().split("\\n\\nCOORDINATES\\n\\n");
                        if(!result.contains(nameos + "\n\t\tAddress : " + parts[0])) {
                            count++;
                            result += "\t\tPerson: " + count + " :\t" + nameos + "\n\t\tAddress : " + parts[0] + "\t\t\n\n";
                        }
                    }
                }
                if (result.equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(booksmsearch.this, "NO RECORD FOUNDED IN DOMAIN", Toast.LENGTH_LONG).show();
                    displaysms.setText("\n\n\t\tNO RECORDS FOUND IN THIRD EYE DATABASE");
                } else {
                    progressDialog.dismiss();
                    countdis.setText(count+". Records present");
                    displaysms.setText(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booksmsearch.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();
            }
        });
    }
}
