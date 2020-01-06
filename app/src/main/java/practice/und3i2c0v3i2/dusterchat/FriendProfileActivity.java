package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import practice.und3i2c0v3i2.dusterchat.databinding.ActivityFriendProfileBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.CHAT_REQUESTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.EMAIL;
import static practice.und3i2c0v3i2.dusterchat.Contract.FACEBOOK;
import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.LINKED_IN;
import static practice.und3i2c0v3i2.dusterchat.Contract.PHONE;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.REQ_STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_RECEIVED;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_SENT;
import static practice.und3i2c0v3i2.dusterchat.Contract.TWITTER;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.WEB_PAGE;

public class FriendProfileActivity extends AppCompatActivity {


    private ActivityFriendProfileBinding binding;
    private DatabaseReference rootRef;
    private DatabaseReference chatReqRef;
    private FirebaseAuth auth;

    private String receiverUserId;
    private String currentUserId;
    private String currentReqStatus;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_profile);

        rootRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS);
        chatReqRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(CHAT_REQUESTS);
        auth = FirebaseAuth.getInstance();

        receiverUserId = getIntent().getStringExtra(FRIEND_ID);
        currentUserId = auth.getCurrentUser().getUid();

        if (!receiverUserId.equals(currentUserId)) {
            binding.friendProfileInfoLayout.sendMsgRequest.setVisibility(View.VISIBLE);
        }


        setSupportActionBar(binding.appbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        currentReqStatus = User.ConnStatus.NEW_REQ;

        user = new User();
        binding.setProfileHandler(this);
        binding.friendProfileInfoLayout.setProfileHandler(this);

        getProfileInfo();
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

    private void handleChatRequest() {

        chatReqRef.child(STATUS_SENT)
                .child(currentUserId)
                .addValueEventListener(chatValueEventListener);

        if (currentReqStatus.equals(User.ConnStatus.NEW_REQ)) {

            chatReqRef.child(STATUS_SENT)
                    .child(currentUserId)
                    .child(receiverUserId)
                    .child(REQ_STATUS)
                    .setValue(User.ConnStatus.SENT_REQ)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                chatReqRef.child(STATUS_RECEIVED)
                                        .child(receiverUserId)
                                        .child(currentUserId)
                                        .child(REQ_STATUS)
                                        .setValue(User.ConnStatus.RECEIVED_REQ)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    binding.friendProfileInfoLayout.sendMsgRequest.setEnabled(true);
                                                    binding.friendProfileInfoLayout.sendMsgRequest.setTextColor(getResources().getColor(R.color.design_default_color_primary_dark));
                                                    binding.friendProfileInfoLayout.sendMsgRequest.setText("Cancel Request");
                                                    currentReqStatus = User.ConnStatus.SENT_REQ;
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    public void sendChatRequest() {
        if (!currentUserId.equals(receiverUserId)) {
            binding.friendProfileInfoLayout.sendMsgRequest.setVisibility(View.VISIBLE);
            binding.friendProfileInfoLayout.sendMsgRequest.setEnabled(false);
            binding.friendProfileInfoLayout.sendMsgRequest.setTextColor(getResources().getColor(R.color.colorGrey));
            handleChatRequest();
        }
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

//            handleChatRequest();
            binding.friendProfileInfoLayout.setUser(user);
            getSupportActionBar().setTitle(user.getUsername() + "'s profile");

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener chatValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild(receiverUserId)) {
                String reqStatus = dataSnapshot
                        .child(receiverUserId)
                        .child(REQ_STATUS)
                        .getValue().toString();

                if (reqStatus.equals(User.ConnStatus.SENT_REQ)) {
                    binding.friendProfileInfoLayout.sendMsgRequest.setText("Cancel Chat Request");
                }
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        if(chatValueEventListener != null) {
            chatReqRef.child(STATUS_SENT)
                    .child(currentUserId)
                    .removeEventListener(chatValueEventListener);
        }

        if(friendProfileEventListener != null) {
            rootRef.child(receiverUserId)
                    .removeEventListener(friendProfileEventListener);
        }

    }
}
