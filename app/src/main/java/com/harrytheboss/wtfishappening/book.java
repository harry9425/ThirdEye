package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class book extends AppCompatActivity {

    public static String username ;
    EditText name;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    DialogFragment dialog;
    String address;
    ScrollView sc;
    String coor1;
    Button qsearch;
    TextView disname,disaddress,sd;

    public void clickon(View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(("geo:0,0?q=" + coor1)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(this, "Address Found Opening GPS..", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }
    public void search(View view)
    {
        username=name.getText().toString().toLowerCase().trim();
        if(username.isEmpty())
        {
            name.setError("Empty field");
            name.requestFocus();
            Toast.makeText(book.this, "Field can't be left emptied", Toast.LENGTH_LONG).show();
        }
        else {

            progressDialog.setMessage("SEARCHING USER IN YOUR PERSONAL RECORDS");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("private").child(LOGIN.name+LOGIN.password+"data");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(username)) {
                        progressDialog.dismiss();
                        address=snapshot.child(username).getValue().toString();
                        sc.setVisibility(View.VISIBLE);
                        String[] parts=address.split("\\n\\nCOORDINATES\\n\\n");
                        coor1=parts[1];
                        disaddress.setText(address);
                        disname.setText(username.toUpperCase());
                        Toast.makeText(book.this, "Record founded in your domain", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        proceed();
                        Toast.makeText(book.this, "NO RECORDS FOUND IN YOUR DOMAIN GOING TO SEARCH IN PUBLIC DOMAIN", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    proceed();
                    Toast.makeText(book.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void publicrecords()
    {
        progressDialog.setMessage("SEARCHING USER IN PUBLIC RECORDS");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("public");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    progressDialog.dismiss();
                    address=snapshot.child(username).getValue().toString();
                    sc.setVisibility(View.VISIBLE);
                    String[] parts=address.split("\\n\\nCOORDINATES\\n\\n");
                    coor1=parts[1];
                    disaddress.setText(address);
                    disname.setText(username.toUpperCase());
                    Toast.makeText(book.this,"Record founded in public domain", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.dismiss();
                    dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    Toast.makeText(book.this, "NO RECORDS FOUND IN PUBLIC DOMAIN ALSO TRY TO ADD NEW DATA", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(book.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void proceed() {
        AlertDialog.Builder al=new AlertDialog.Builder(book.this);
        al.setTitle("RECORD NOT FOUND IN YOUR PERSONAL DOMAIN");
        al.setMessage("WANT TO SEARCH IT IN PUBLIC DOMAIN??");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               publicrecords();
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
    public void proceedtodelte(View view) {
        username=name.getText().toString().toLowerCase().trim();
        if(username.isEmpty())
        {
            name.setError("Empty field");
            name.requestFocus();
            Toast.makeText(book.this, "Field can't be left emptied", Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder al = new AlertDialog.Builder(book.this);
            al.setTitle("DELETE THE DATA");
            al.setMessage("Data of the user : " + username + " will be deleted only from your private database not the national one.\nSo you want to delete the " + username + "'s info from private server ?");
            al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteit();
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
    }
    private void deleteit()
    {
        progressDialog.setMessage("SEARCHING USER IN YOUR PERSONAL RECORDS");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ADDRESS BOOK").child("private").child(LOGIN.name+LOGIN.password+"data");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    progressDialog.dismiss();
                    mDatabase.child(username).removeValue();
                    Toast.makeText(book.this, "Record founded in your domain, deleting it.....", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(book.this, "NO RECORDS FOUND IN YOUR DOMAIN, USER DATA DOESN'T EXIST", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(book.this, "CAN'T CONNECT TO INTERNET", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void smartsrch(View view)
    {
        Intent intent=new Intent(this,booksmsearch.class);
        startActivity(intent);
    }
    public void add(View view)
    {
        Intent intent=new Intent(this,bookadd.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        name=(EditText) findViewById(R.id.name);
        sc = (ScrollView) findViewById(R.id.scroll);
        sc.setVisibility(View.INVISIBLE);
        disname=(TextView) findViewById(R.id.zizi);
        disaddress=(TextView) findViewById(R.id.displayaddressscroll);
        progressDialog = new ProgressDialog(book.this);
         dialog = new book.MyDialogFragment();
    }
    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("MESSAGE FROM THIRD EYE");
            builder.setMessage("No records are found either in public domain and your personal domain.Add new data if you want to");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            return builder.create();
        }
    }
}
