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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    EditText etName, etEmail, etPassword, etConPassword, etPhone;
    Button mRegisterBtn;
    TextView mLoginText;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName=findViewById(R.id.etNameId);
        etEmail=findViewById(R.id.etEmailId);
        etPhone=findViewById(R.id.etPhoneId);
        etPassword=findViewById(R.id.etPasswordId);
        etConPassword=findViewById(R.id.etConfirmPasswordId);
        mRegisterBtn=findViewById(R.id.btnRegister);
        mLoginText=findViewById(R.id.textLogin);
        progressBar=findViewById(R.id.progressBarId);

        firebaseAuth= FirebaseAuth.getInstance();

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

                if (TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required");
                    return;
                }if (TextUtils.isEmpty(password)){
                    etPassword.setError("Password is required");
                    return;
                }if (password.length() < 6){
                    etPassword.setError("Password is must be >= 6 character");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Register the user in firebase----------------------------
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            //send email verification link
                            

                            Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
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
}