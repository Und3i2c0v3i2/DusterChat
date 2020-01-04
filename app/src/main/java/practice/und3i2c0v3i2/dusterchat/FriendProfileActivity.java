package practice.und3i2c0v3i2.dusterchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;

public class FriendProfileActivity extends AppCompatActivity {

    private String receiverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        receiverUserId = getIntent().getStringExtra(FRIEND_ID);
    }
}
