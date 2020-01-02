package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
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

import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.UID;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private String currentUID;
    private String username;
    private String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        binding.setLifecycleOwner(this);
        binding.setProfileHandler(this);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        binding.setAuth(auth);
        binding.setRootRef(rootRef);
        currentUID = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        setSupportActionBar(binding.profileToolbar.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getProfileInfo();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileInfo() {

        rootRef.child(USERS)
                .child(currentUID)
                .addValueEventListener(valueEventListener);

    }

    public void updateProfile() {

        username = binding.profileUsername.getText().toString();
        status = binding.profileStatus.getText().toString();


        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();

        } else {

            HashMap<String, String> profileInfo = new HashMap<>();
            profileInfo.put(UID, currentUID);
            profileInfo.put(USERNAME, username);
            profileInfo.put(STATUS, status);

            rootRef.child(USERS)
                    .child(currentUID)
                    .setValue(profileInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToHomePage();
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    public void sendUserToHomePage() {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();

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
                status = "";
            }

            binding.profileUsername.setText(username);
            binding.profileStatus.setText(status);
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
