package com.fyp.communicationmodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;

    private Button updateButton;
    private EditText userID, fullName;
    private CircleImageView userProfilePicture;
    private Toolbar settingToolbar;

    private String currentUserID;
    private ProgressDialog loadingBar;
    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateFieldsValue();
            }
        });

        RetrieveUserInformation();

        userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }

    private void InitializeFields() {
        settingToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Update Profile");

        updateButton = (Button) findViewById(R.id.updateSettingButton);
        userID = (EditText) findViewById(R.id.setUserID);
        fullName = (EditText) findViewById(R.id.setFullName);
        userProfilePicture = (CircleImageView) findViewById(R.id.setProfileImage);
        loadingBar = new ProgressDialog(this);
    }

    private void RetrieveUserInformation() {
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("userID")) && (dataSnapshot.hasChild("fullName")) && (dataSnapshot.hasChild("profileImage"))){
                    String retrieveUserID = dataSnapshot.child("userID").getValue().toString();
                    String retrieveFullName = dataSnapshot.child("fullName").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    userID.setText(retrieveUserID);
                    fullName.setText(retrieveFullName);
                    Picasso.get().load(retrieveProfileImage).into(userProfilePicture);
                }
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("userID")) && (dataSnapshot.hasChild("fullName"))){
                    String retrieveUserID = dataSnapshot.child("userID").getValue().toString();
                    String retrieveFullName = dataSnapshot.child("fullName").getValue().toString();

                    userID.setText(retrieveUserID);
                    fullName.setText(retrieveFullName);
                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("userID"))) {

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateFieldsValue() {
        String setFullName = fullName.getText().toString();
        String setUserID = userID.getText().toString();

        if(TextUtils.isEmpty(setFullName)){
            Toast.makeText(this, "Please enter your full name.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(setUserID)){
            Toast.makeText(this, "Please enter your user ID.", Toast.LENGTH_SHORT).show();
        }
        else{
            RootRef.child("Users").child(currentUserID).child("fullName").setValue(setFullName);
            RootRef.child("Users").child(currentUserID).child("userID").setValue(setUserID).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();

                                RootRef.child("Users").child(currentUserID).child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingActivity.this, "Image saved in database successfuly", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else{
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingActivity.this, "Error: " + message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}