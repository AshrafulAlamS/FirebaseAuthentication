package com.example.firebaseauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mRegisterText,mForgetPass;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail=findViewById(R.id.etEmail);
        mPassword=findViewById(R.id.etPassword);
        mLoginBtn=findViewById(R.id.btnLogin);
        mRegisterText=findViewById(R.id.textRegister);
        mForgetPass=findViewById(R.id.textForgetPassword);
        progressBar=findViewById(R.id.progressBarLogin);

        firebaseAuth=FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }if (password.length() < 6){
                    mPassword.setError("Password is must be >= 6 character");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Login Part-----------------------------------------------------------------
                //authenticate the user
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            LoginActivity.this.finish();//to finish this activity after login

                        }else {
                            Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });

        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Forget Password Part-------------------------------------------
        mForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetMail =new EditText(v.getContext());

                AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter you email to receive reset link");
                passwordResetDialog.setView(resetMail);
                
                passwordResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String mail = resetMail.getText().toString();
                            if (mail.isEmpty() || resetMail.getText().toString().isEmpty()){
                                Toast.makeText(LoginActivity.this, "You not enter any email!", Toast.LENGTH_SHORT).show();
                                return;
                            }else{

                                //extract the email and send reset link
                                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Error! Reset Link is not sent" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog

                    }
                });

                passwordResetDialog.create().show();
            }
        });

    }
}