package com.example.firebaseauthenticationapp;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button logoutBtn, verifyBtn;
    TextView verifyMsg;
    private FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutBtn=findViewById(R.id.btnLogout);
        verifyBtn=findViewById(R.id.verifyBtnId);
        verifyMsg=findViewById(R.id.verifyMsg);

        //firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();

        //check the email verification
        if (!firebaseUser.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
        }


        verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //sendVerificationLink();
                }
            });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutUser();
            }
        });


    }

    private void logoutUser(){
        //Logout part Firebase---------------------------------------
        FirebaseAuth.getInstance().signOut(); //Logout user from the app
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    //send verifiactin link
    public void sendVerificationLink(){
        Context c =verifyBtn.getContext();

        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(MainActivity.this, "A verification link has been sent to your email", Toast.LENGTH_SHORT).show();

                TextView reSentMail =new TextView(c);
                AlertDialog.Builder showMessageDialog = new AlertDialog.Builder(c);
                showMessageDialog.setTitle("Check Email");
                showMessageDialog.setMessage("A verification link is sent to you email. Please verify your email and login again");
                showMessageDialog.setView(reSentMail);
                showMessageDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logoutUser();
                    }
                });
                showMessageDialog.create().show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
            }
        });
    }
}