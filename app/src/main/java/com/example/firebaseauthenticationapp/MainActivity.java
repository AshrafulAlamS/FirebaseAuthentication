package com.example.firebaseauthenticationapp;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button logoutBtn, verifyBtn, updateImageBtn;
    private TextView verifyMsg, fullName, email, phone;
    private ImageView profileImage;
    private final FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private String userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutBtn=findViewById(R.id.btnLogout);
        verifyBtn=findViewById(R.id.verifyBtnId);
        verifyMsg=findViewById(R.id.verifyMsg);
        fullName=findViewById(R.id.profileNameId);
        email=findViewById(R.id.profileEmailId);
        phone=findViewById(R.id.profilePhoneId);
        profileImage=findViewById(R.id.profileImageId);
        updateImageBtn=findViewById(R.id.updateImageBtn);

        //firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = firebaseAuth.getCurrentUser().getUid();

        //Retrive data from FirebaseFirestore
        //retriveProfileData();
        DocumentReference documentReference = firestore.collection("users").document(userId);
        if (userId !=null){
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    email.setText(value.getString("email"));
                    fullName.setText(value.getString("fName"));
                    phone.setText(value.getString("phone"));
                }
            });
        }

        //check the email verification
        if (!firebaseUser.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);
        }


        verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    reSendVerificationLink();
                }
            });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutUser();
            }
        });

        //retrive image
        retriveProfileImageInitially();

        updateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }

    //Retrive Profile Image Initially
    private void retriveProfileImageInitially(){
        StorageReference profileRef = storageReference.child("users/"+firebaseUser.getUid()+"/Profile-Picture");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    // onActitityResult for Open Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri){
        //Upload image to firebase storage
        StorageReference fileRef = storageReference.child("users/"+firebaseUser.getUid()+"/Profile-Picture");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
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
    public void reSendVerificationLink(){
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

                        verifyMsg.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.GONE);

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

    //Retrive data from FirebaseFirestore
    /*private void retriveProfileData(){
        DocumentReference documentReference = firestore.collection("users").document(userId);
        if (userId !=null){
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    email.setText(value.getString("email"));
                    fullName.setText(value.getString("fName"));
                    phone.setText(value.getString("phone"));
                }
            });
        }
    }*/
}