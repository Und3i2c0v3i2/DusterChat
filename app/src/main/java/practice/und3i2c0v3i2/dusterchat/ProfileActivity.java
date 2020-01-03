package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Objects;

import practice.und3i2c0v3i2.dusterchat.databinding.ActivityProfileBinding;

import static practice.und3i2c0v3i2.dusterchat.Contract.EMAIL;
import static practice.und3i2c0v3i2.dusterchat.Contract.FACEBOOK;
import static practice.und3i2c0v3i2.dusterchat.Contract.LINKED_IN;
import static practice.und3i2c0v3i2.dusterchat.Contract.PHONE;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.TWITTER;
import static practice.und3i2c0v3i2.dusterchat.Contract.UID;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.WEB_PAGE;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private String currentUID;
    private String username;
    private String status;
    private String phone;
    private String email;
    private String web;
    private String linkedIn;
    private String facebook;
    private String twitter;
    private InputMethodManager inputMethodManager;



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

        binding.setAuth(auth);
        binding.setRootRef(rootRef);
        binding.profileInfoHeaderLayout.setAuth(auth);
        binding.profileInfoHeaderLayout.setRootRef(rootRef);
        binding.profileInfoPersonalLayout.setAuth(auth);
        binding.profileInfoPersonalLayout.setRootRef(rootRef);
        binding.profileInfoSocialLayout.setAuth(auth);
        binding.profileInfoSocialLayout.setRootRef(rootRef);
        currentUID = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setSupportActionBar(binding.profileToolbar.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getProfileInfo();

        binding.btnUpdateProfile.setVisibility(View.INVISIBLE);
        binding.btnCancelUpdate.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getProfileInfo() {

        rootRef.child(USERS)
                .child(currentUID)
                .addValueEventListener(valueEventListener);

    }

    public void finishProfileUpdate() {
        getProfileInfo();
        binding.btnUpdateProfile.setVisibility(View.INVISIBLE);
        binding.btnCancelUpdate.setVisibility(View.INVISIBLE);

        binding.profileInfoHeaderLayout.displayUsername.setEnabled(false);
        binding.profileInfoHeaderLayout.displayStatus.setEnabled(false);

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

        binding.profileInfoHeaderLayout.displayUsername.requestFocus();
        binding.profileInfoHeaderLayout.displayUsername.setSelection(binding.profileInfoHeaderLayout.displayUsername.getText().length());
        inputMethodManager.showSoftInput(binding.profileInfoHeaderLayout.displayUsername, InputMethodManager.SHOW_IMPLICIT);
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

        getUserInputs();


        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();

        } else {

            HashMap<String, String> profileInfo = new HashMap<>();
            profileInfo.put(UID, currentUID);
            profileInfo.put(USERNAME, username);
            profileInfo.put(STATUS, status);
            profileInfo.put(PHONE, phone);
            profileInfo.put(EMAIL, email);
            profileInfo.put(WEB_PAGE, web);
            profileInfo.put(LINKED_IN, linkedIn);
            profileInfo.put(FACEBOOK, facebook);
            profileInfo.put(TWITTER, twitter);

            rootRef.child(USERS)
                    .child(currentUID)
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

    private void getUserInputs() {

        username = binding.profileInfoHeaderLayout.displayUsername.getText().toString();
        status = binding.profileInfoHeaderLayout.displayStatus.getText().toString();

        phone = binding.profileInfoPersonalLayout.displayPhone.getText().toString();
        email = binding.profileInfoPersonalLayout.displayEmail.getText().toString();
        web = binding.profileInfoPersonalLayout.displayWeb.getText().toString();

        linkedIn = binding.profileInfoSocialLayout.displayLinkedIn.getText().toString();
        facebook = binding.profileInfoSocialLayout.displayFacebook.getText().toString();
        twitter = binding.profileInfoSocialLayout.displayTwitter.getText().toString();
    }


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists() && dataSnapshot.hasChild(USERNAME)) {
                username = dataSnapshot.child(USERNAME).getValue().toString();
            } else {
                username = "";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(STATUS)) {
                status = dataSnapshot.child(STATUS).getValue().toString();
            } else {
                status = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(PHONE)) {
                phone = dataSnapshot.child(PHONE).getValue().toString();
            } else {
                phone = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(EMAIL)) {
                email = dataSnapshot.child(EMAIL).getValue().toString();
            } else {
                email = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(WEB_PAGE)) {
                web = dataSnapshot.child(WEB_PAGE).getValue().toString();
            } else {
                web = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(LINKED_IN)) {
                linkedIn = dataSnapshot.child(LINKED_IN).getValue().toString();
            } else {
                linkedIn = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(FACEBOOK)) {
                facebook = dataSnapshot.child(FACEBOOK).getValue().toString();
            } else {
                facebook = "--";
            }

            if(dataSnapshot.exists() && dataSnapshot.hasChild(TWITTER)) {
                twitter = dataSnapshot.child(TWITTER).getValue().toString();
            } else {
                twitter = "--";
            }

            binding.profileInfoHeaderLayout.displayUsername.setText(username);
            binding.profileInfoHeaderLayout.displayStatus.setText(status);
            binding.profileInfoPersonalLayout.displayPhone.setText(phone);
            binding.profileInfoPersonalLayout.displayEmail.setText(email);
            binding.profileInfoPersonalLayout.displayWeb.setText(web);
            binding.profileInfoSocialLayout.displayLinkedIn.setText(linkedIn);
            binding.profileInfoSocialLayout.displayFacebook.setText(facebook);
            binding.profileInfoSocialLayout.displayTwitter.setText(twitter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onDestroy() {

        rootRef.removeEventListener(valueEventListener);
        rootRef = null;
        auth = null;
        super.onDestroy();
    }
}
