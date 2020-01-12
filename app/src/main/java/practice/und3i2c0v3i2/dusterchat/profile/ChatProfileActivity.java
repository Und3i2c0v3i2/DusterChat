package practice.und3i2c0v3i2.dusterchat.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityChatProfileBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;

public class ChatProfileActivity extends AppCompatActivity {


    private ActivityChatProfileBinding binding;
    private DatabaseReference rootRef;
    private FirebaseAuth auth;

    private String receiverUserId;
    private String currentUserId;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_profile);

        user = new User();

        initializeFirebaseRefs();

        receiverUserId = getIntent().getStringExtra(FRIEND_ID);


        setSupportActionBar(binding.appbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!receiverUserId.equals(currentUserId)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ChatFriendProfileFragment.newInstance(currentUserId, receiverUserId))
                    .commit();
        } else {

            Intent profileIntent = new Intent(this, ProfileActivity.class);
            finish();
            startActivity(profileIntent);

        }


        getProfileInfo();
    }

    private void initializeFirebaseRefs() {

        rootRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_USERS);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void getProfileInfo() {

        rootRef.child(receiverUserId)
                .addValueEventListener(friendProfileEventListener);

    }


    private ValueEventListener friendProfileEventListener = new ValueEventListener() {
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


            binding.setUser(user);
            getSupportActionBar().setTitle(user.getUsername() + "'s profile");

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (friendProfileEventListener != null) {
            rootRef.child(receiverUserId)
                    .removeEventListener(friendProfileEventListener);
        }

    }

}
