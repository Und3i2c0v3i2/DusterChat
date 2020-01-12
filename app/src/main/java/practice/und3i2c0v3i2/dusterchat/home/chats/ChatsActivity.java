package practice.und3i2c0v3i2.dusterchat.home.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import practice.und3i2c0v3i2.dusterchat.ChatListener;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityChatsBinding;
import practice.und3i2c0v3i2.dusterchat.model.Message;
import practice.und3i2c0v3i2.dusterchat.model.User;
import practice.und3i2c0v3i2.dusterchat.util.UtilDateConverter;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.FROM;
import static practice.und3i2c0v3i2.dusterchat.Contract.MESSAGE;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CHAT_MESSAGES;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.TIMESTAMP;
import static practice.und3i2c0v3i2.dusterchat.Contract.TYPE;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;

public class ChatsActivity extends AppCompatActivity implements ChatListener {

    private ActivityChatsBinding binding;
    private String currentUserID;
    private String friendID;
    private String friendUsername;
    private String friendImg;

    private DatabaseReference chatRef;
    private FirebaseAuth auth;

    private ChatsAdapter adapter;
    private List<Message> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);


        friendID = getIntent().getStringExtra(FRIEND_ID);
        friendUsername = getIntent().getStringExtra(USERNAME);
        friendImg = getIntent().getStringExtra(PROFILE_IMG);

        User user = new User();
        user.setUsername(friendUsername);
        user.setImgUrl(friendImg);
        binding.chatToolbar.setUser(user);

        setSupportActionBar(binding.chatToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.chatLayoutSendMsg.setChatHandler(this);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

        chatRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_CHAT_MESSAGES);

        list = new ArrayList<>();
        adapter = new ChatsAdapter(list);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        getMessages();

    }

    private void getMessages() {

        chatRef.child(currentUserID)
                .child(friendID)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        list.add(message);
                        adapter.notifyDataSetChanged();
                        binding.recyclerView.smoothScrollToPosition(list.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
                });

    }

    @Override
    public void sendMsg() {

        String msg = binding.chatLayoutSendMsg.chatSendMsgInput.getText().toString();

        if (!TextUtils.isEmpty(msg)) {

            String timestamp = UtilDateConverter.convertDateTimeToString(new Date());

            String msgSenderRef = currentUserID + "/" + friendID;
            String msgReceiverRef = friendID + "/" + currentUserID;

            DatabaseReference privateChatRef = chatRef.child(currentUserID)
                    .child(friendID)
                    .push();

            String msgKey = privateChatRef.getKey();

            HashMap<String, Object> msgInfo = new HashMap<>();
            msgInfo.put(MESSAGE, msg);
            msgInfo.put(TYPE, "text");
            msgInfo.put(FROM, currentUserID);
            msgInfo.put(TIMESTAMP, timestamp);

            HashMap<String, Object> msgDetails = new HashMap<>();
            msgDetails.put(msgSenderRef + "/" + msgKey, msgInfo);
            msgDetails.put(msgReceiverRef + "/" + msgKey, msgInfo);

            chatRef.updateChildren(msgDetails);

        }

        binding.chatLayoutSendMsg.chatSendMsgInput.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
