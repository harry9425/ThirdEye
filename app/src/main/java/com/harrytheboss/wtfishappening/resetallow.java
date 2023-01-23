package com.harrytheboss.wtfishappening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class resetallow extends AppCompatActivity {

    ProgressDialog progressDialog;
    String verificationCodeBySystem;
    EditText phoneNoEnteredByTheUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetallow);
        progressDialog = new ProgressDialog(resetallow.this);
        progressDialog.setMessage("WAITING FOR OTP");
        progressDialog.show();
        sendVerificationCodeToUser(forgetpass.phoneofuser);
        Button verifyplz=(Button) findViewById(R.id.letsgo);
        phoneNoEnteredByTheUser =(EditText) findViewById(R.id.otpmanual);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        verifyplz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = phoneNoEnteredByTheUser.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    phoneNoEnteredByTheUser.setError("Wrong OTP...");
                    phoneNoEnteredByTheUser.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }
    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(resetallow.this, "TRY AGAIN LATER", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String codeByUser) {

        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(resetallow.this, "WRONG OTP", Toast.LENGTH_SHORT).show();
            Intent intent34 = new Intent(resetallow.this, restdone.class);
            startActivity(intent34);
        }

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(resetallow.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(resetallow.this, "USER VERIFIED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                            Intent intent34 = new Intent(resetallow.this, restdone.class);
                            startActivity(intent34);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(resetallow.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
