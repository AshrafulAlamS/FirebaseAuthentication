package com.example.firebaseauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    EditText etFullName, etEmail, etPassword, etConPassword, etPhone;
    Button mRegisterBtn;
    TextView mLoginText;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName=findViewById(R.id.etNameId);
        etEmail=findViewById(R.id.etEmailId);
        etPhone=findViewById(R.id.etPhoneId);
        etPassword=findViewById(R.id.etPasswordId);
        etConPassword=findViewById(R.id.etConfirmPasswordId);
        mRegisterBtn=findViewById(R.id.btnRegister);
        mLoginText=findViewById(R.id.textLogin);
        progressBar=findViewById(R.id.progressBarId);

        firebaseAuth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //check if user is already registered
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        //Registration part--------------------------------------------------------
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString();
                String phone = etPhone.getText().toString();
                String confirmPassword = etConPassword.getText().toString();

                if (fullName.isEmpty()){
                    etFullName.setError("Name is required");
                    return;
                }if (TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required");
                    return;
                }if (phone.isEmpty()){
                    etPhone.setError("Phone number is required");
                    return;
                }if (TextUtils.isEmpty(password)){
                    etPassword.setError("Password is required");
                    return;
                }if (password.length() < 6){
                    etPassword.setError("Password is must be >= 6 character");
                    return;
                }if (confirmPassword.isEmpty()){
                    etConPassword.setError("Confirm password is required");
                    return;
                }if (!password.equals(confirmPassword)){
                    etConPassword.setError("Password not match!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //User Registration with Firebse----------------------------
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();

                            storeUserDataInFirestore();

                            sendEmailVerificationLink();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //Store user profile data in Firebase FireStore--------------------
    private void storeUserDataInFirestore(){
        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("users").document(userID);
        Map<String, Object> user =new HashMap<>();
        user.put("fName", etFullName.getText().toString());
        user.put("email", etEmail.getText().toString().trim());
        user.put("phone", etPhone.getText().toString());

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: user profile is created for " + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    //send email verification link------------------------
    private void sendEmailVerificationLink(){

        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
            }
        });
    }
}