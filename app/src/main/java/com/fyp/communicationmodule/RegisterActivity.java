package com.fyp.communicationmodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountButton, HaveAccountButton, UploadPhotoButton;
    private EditText UserFullName, UserID, UserEmail, UserPassword, UserConfirmPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;
    private Uri resultUri=null;
    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        HaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            { SendUserToLoginActivity(); }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });

        UploadPhotoButton.setOnClickListener(new View.OnClickListener() {
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
        CreateAccountButton = (Button) findViewById(R.id.register_button);
        HaveAccountButton = (Button) findViewById(R.id.haveAccount_button);
        UploadPhotoButton = (Button) findViewById(R.id.upload_photo_button);
        UserFullName = (EditText) findViewById(R.id.register_fullName);
        UserID = (EditText) findViewById(R.id.register_userName);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirmPassword);

        loadingBar = new ProgressDialog(this);
    }

    private void CreateNewAccount() {
        final String fullName = UserFullName.getText().toString();
        final String username = UserID.getText().toString();
        final String email = UserEmail.getText().toString();
        final String password = UserPassword.getText().toString();
        final String confirmPassword = UserConfirmPassword.getText().toString();
        final String[] downloadUrl = {null};

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we wre creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                final String currentUserID = mAuth.getCurrentUser().getUid();
                                if(resultUri!=null){
                                    StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");
                                    filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();

                                                    RootRef.child("Users").child(currentUserID).child("profileImage").setValue(downloadUrl)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){

                                                                    }
                                                                    else{

                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });
                                }
                                HashMap<String, String> userProfile = new HashMap<>();
                                userProfile.put("fullName", fullName);
                                userProfile.put("userID", username);
                                userProfile.put("email", email);
                                userProfile.put("password", password);

                                RootRef.child("Users").child(currentUserID).setValue(userProfile);

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

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
                resultUri = result.getUri();
            }
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
