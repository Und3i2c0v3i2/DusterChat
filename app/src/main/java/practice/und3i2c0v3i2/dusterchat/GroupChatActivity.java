package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import practice.und3i2c0v3i2.dusterchat.databinding.ActivityGroupChatBinding;
import practice.und3i2c0v3i2.dusterchat.util.UtilDateConverter;

import static practice.und3i2c0v3i2.dusterchat.Contract.GROUPS;
import static practice.und3i2c0v3i2.dusterchat.Contract.MESSAGE;
import static practice.und3i2c0v3i2.dusterchat.Contract.TIMESTAMP;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.BUNDLE_GROUP_NAME;

public class GroupChatActivity extends AppCompatActivity implements ChatListener {


    private ActivityGroupChatBinding binding;
    private String groupName,
            uId,
            username;
    private FirebaseAuth auth;
    private DatabaseReference usersRef,
            groupRef,
            groupMsgRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat);

        binding.chatLayoutSendMsg.setChatHandler(this);

        if (getIntent() != null) {
            groupName = getIntent().getStringExtra(BUNDLE_GROUP_NAME);
        }


        setSupportActionBar(binding.groupChatToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("\tGroup: " + groupName);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_group);
            getSupportActionBar().setIcon(drawable);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS);
        groupRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(GROUPS)
                .child(groupName);
        uId = auth.getCurrentUser().getUid();

        getCurrentUserUsername();

    }

    private void getCurrentUserUsername() {
        usersRef.child(uId)
                .addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void sendMsg() {
        String msg = binding.chatLayoutSendMsg.chatSendMsgInput.getText().toString();
        String msgKey = groupRef.push().getKey();

        if (!TextUtils.isEmpty(msg)) {

            String timestamp = UtilDateConverter.convertDateTimeToString(new Date());

            HashMap<String, Object> groupMsgKey = new HashMap<>();
            groupRef.updateChildren(groupMsgKey);
            groupMsgRef = groupRef.child(msgKey);

            HashMap<String, Object> msgInfo = new HashMap<>();
            msgInfo.put(USERNAME, username);
            msgInfo.put(MESSAGE, msg);
            msgInfo.put(TIMESTAMP, timestamp);

            groupMsgRef.updateChildren(msgInfo);

        }

        binding.chatLayoutSendMsg.chatSendMsgInput.setText("");
        binding.chatLayoutDisplayMsg.chatScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getPrevMessages();

    }

    private void getPrevMessages() {
        groupRef.addChildEventListener(childEventListener);
    }

    private void displayMessages(DataSnapshot dataSnapshot) {

        StringBuilder message = new StringBuilder();
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String chatMessage = ((DataSnapshot) iterator.next()).getValue().toString();
            String chatTimestamp = ((DataSnapshot) iterator.next()).getValue().toString();
            String chatUsername = ((DataSnapshot) iterator.next()).getValue().toString();

            message.append(chatUsername)
                    .append(":\n")
                    .append(chatMessage)
                    .append("\n")
                    .append(chatTimestamp)
                    .append("\n\n");
        }

        binding.chatLayoutDisplayMsg.chatDisplayMsg
                .append(message.toString());

        binding.chatLayoutDisplayMsg.chatScrollView.post(new Runnable() {
            @Override
            public void run() {
                binding.chatLayoutDisplayMsg.chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                username = dataSnapshot.child(USERNAME)
                        .getValue()
                        .toString();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (dataSnapshot.exists()) {
                displayMessages(dataSnapshot);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (dataSnapshot.exists()) {
                displayMessages(dataSnapshot);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(valueEventListener != null) {
            usersRef.child(uId)
                    .removeEventListener(valueEventListener);
        }
        if(childEventListener != null) {
            groupRef.removeEventListener(childEventListener);
        }

    }

}
