package practice.und3i2c0v3i2.dusterchat.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityProfileBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.EMAIL;
import static practice.und3i2c0v3i2.dusterchat.Contract.FACEBOOK;
import static practice.und3i2c0v3i2.dusterchat.Contract.LINKED_IN;
import static practice.und3i2c0v3i2.dusterchat.Contract.PHONE;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMAGES;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.TWITTER;
import static practice.und3i2c0v3i2.dusterchat.Contract.CURRENT_UID;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.WEB_PAGE;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQ_CODE = 3000;

    private ActivityProfileBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private StorageReference profileImgRef;

    private User user;

    private InputMethodManager inputMethodManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        binding.setLifecycleOwner(this);
        binding.setProfileHandler(this);
        binding.profileInfoHeaderLayout.setProfileHandler(this);
        binding.profileInfoPersonalLayout.setProfileHandler(this);
        binding.profileInfoSocialLayout.setProfileHandler(this);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        profileImgRef = FirebaseStorage.getInstance().getReference().child(PROFILE_IMAGES);

        user = new User();
        user.setUid(auth.getCurrentUser().getUid());

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        progressDialog = new ProgressDialog(this);

        setSupportActionBar(binding.profileToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getProfileInfo();

        binding.btnUpdateProfile.setVisibility(View.INVISIBLE);
        binding.btnCancelUpdate.setVisibility(View.INVISIBLE);
        binding.profileInfoHeaderLayout.displayProfileImage.setEnabled(false);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getProfileInfo() {

        rootRef.child(NODE_USERS)
                .child(user.getUid())
                .addValueEventListener(userProfileEventListener);

    }

    public void finishProfileUpdate() {
        getProfileInfo();
        binding.btnUpdateProfile.setVisibility(View.INVISIBLE);
        binding.btnCancelUpdate.setVisibility(View.INVISIBLE);

        binding.profileInfoHeaderLayout.displayUsername.setEnabled(false);
        binding.profileInfoHeaderLayout.displayStatus.setEnabled(false);
        binding.profileInfoHeaderLayout.displayProfileImage.setEnabled(false);

        binding.profileInfoPersonalLayout.displayPhone.setEnabled(false);
        binding.profileInfoPersonalLayout.displayEmail.setEnabled(false);
        binding.profileInfoPersonalLayout.displayWeb.setEnabled(false);

        binding.profileInfoSocialLayout.displayLinkedIn.setEnabled(false);
        binding.profileInfoSocialLayout.displayFacebook.setEnabled(false);
        binding.profileInfoSocialLayout.displayTwitter.setEnabled(false);
    }

    public void editProfile() {
        binding.btnUpdateProfile.setVisibility(View.VISIBLE);
        binding.btnCancelUpdate.setVisibility(View.VISIBLE);

        binding.profileInfoHeaderLayout.displayUsername.setEnabled(true);
        binding.profileInfoHeaderLayout.displayStatus.setEnabled(true);
        binding.profileInfoHeaderLayout.displayProfileImage.setEnabled(true);

        binding.profileInfoHeaderLayout.displayUsername.requestFocus();
        binding.profileInfoHeaderLayout.displayUsername.setSelection(binding.profileInfoHeaderLayout.displayUsername.getText().length());
        inputMethodManager.showSoftInput(binding.profileInfoHeaderLayout.displayUsername, InputMethodManager.SHOW_IMPLICIT);
    }

    public void selectImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
    }


    public void editPersonal() {
        binding.btnUpdateProfile.setVisibility(View.VISIBLE);
        binding.btnCancelUpdate.setVisibility(View.VISIBLE);

        binding.profileInfoPersonalLayout.displayPhone.setEnabled(true);
        binding.profileInfoPersonalLayout.displayEmail.setEnabled(true);
        binding.profileInfoPersonalLayout.displayWeb.setEnabled(true);

        binding.profileInfoPersonalLayout.displayPhone.requestFocus();
        binding.profileInfoPersonalLayout.displayPhone.setSelection(binding.profileInfoPersonalLayout.displayPhone.getText().length());
        inputMethodManager.showSoftInput(binding.profileInfoPersonalLayout.displayPhone, InputMethodManager.SHOW_IMPLICIT);

    }


    public void editSocial() {
        binding.btnUpdateProfile.setVisibility(View.VISIBLE);
        binding.btnCancelUpdate.setVisibility(View.VISIBLE);

        binding.profileInfoSocialLayout.displayLinkedIn.setEnabled(true);
        binding.profileInfoSocialLayout.displayFacebook.setEnabled(true);
        binding.profileInfoSocialLayout.displayTwitter.setEnabled(true);

        binding.profileInfoSocialLayout.displayLinkedIn.requestFocus();
        binding.profileInfoSocialLayout.displayLinkedIn.setSelection(binding.profileInfoSocialLayout.displayLinkedIn.getText().length());
        inputMethodManager.showSoftInput(binding.profileInfoSocialLayout.displayLinkedIn, InputMethodManager.SHOW_IMPLICIT);
    }

    public void updateProfile() {

        if (user.getUsername().isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();

        } else {

            HashMap<String, String> profileInfo = new HashMap<>();
            profileInfo.put(PROFILE_IMG, user.getImgUrl());
            profileInfo.put(CURRENT_UID, user.getUid());
            profileInfo.put(USERNAME, user.getUsername());
            profileInfo.put(STATUS, user.getStatus());
            profileInfo.put(PHONE, user.getPhone());
            profileInfo.put(EMAIL, user.getEmail());
            profileInfo.put(WEB_PAGE, user.getWeb());
            profileInfo.put(LINKED_IN, user.getLinkedIn());
            profileInfo.put(FACEBOOK, user.getFacebook());
            profileInfo.put(TWITTER, user.getTwitter());


            rootRef.child(NODE_USERS)
                    .child(user.getUid())
                    .setValue(profileInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                finishProfileUpdate();
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null) {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Setting profile image");
                progressDialog.setMessage("Your profile image is updating, please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Uri resultUri = result.getUri();


                StorageReference storageReference = profileImgRef.child(user.getUid() + ".jpg");
                storageReference.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    task.getResult()
                                            .getStorage()
                                            .getDownloadUrl()
                                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if(task.isSuccessful()) {
                                                        user.setImgUrl(task.getResult().toString());
                                                        updateProfile();
                                                    }
                                                }
                                            });

                                    rootRef.child(NODE_USERS)
                                            .child(user.getUid())
                                            .child(PROFILE_IMG)
                                            .setValue(user.getImgUrl())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        updateProfile();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });

                                    updateProfile();
                                }
                            }
                        });
            }

        }


    }

    private ValueEventListener userProfileEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists() && dataSnapshot.hasChild(PROFILE_IMG)) {
                user.setImgUrl(dataSnapshot.child(PROFILE_IMG).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(USERNAME)) {
                user.setUsername(dataSnapshot.child(USERNAME).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(STATUS)) {
                user.setStatus(dataSnapshot.child(STATUS).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(PHONE)) {
                user.setPhone(dataSnapshot.child(PHONE).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(EMAIL)) {
                user.setEmail(dataSnapshot.child(EMAIL).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(WEB_PAGE)) {
                user.setWeb(dataSnapshot.child(WEB_PAGE).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(LINKED_IN)) {
                user.setLinkedIn(dataSnapshot.child(LINKED_IN).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(FACEBOOK)) {
                user.setFacebook(dataSnapshot.child(FACEBOOK).getValue().toString());
            }

            if (dataSnapshot.exists() && dataSnapshot.hasChild(TWITTER)) {
                user.setTwitter(dataSnapshot.child(TWITTER).getValue().toString());
            }

            binding.profileInfoHeaderLayout.setUser(user);
            binding.profileInfoPersonalLayout.setUser(user);
            binding.profileInfoSocialLayout.setUser(user);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(userProfileEventListener != null) {
            rootRef.child(NODE_USERS)
                    .child(user.getUid())
                    .removeEventListener(userProfileEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}
