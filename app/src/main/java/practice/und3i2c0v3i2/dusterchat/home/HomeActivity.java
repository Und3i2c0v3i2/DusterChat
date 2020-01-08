package practice.und3i2c0v3i2.dusterchat.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


import practice.und3i2c0v3i2.dusterchat.find_friends.FindFriendsActivity;
import practice.und3i2c0v3i2.dusterchat.home.groups.GroupChatActivity;
import practice.und3i2c0v3i2.dusterchat.register_login.LoginActivity;
import practice.und3i2c0v3i2.dusterchat.OnItemClickListener;
import practice.und3i2c0v3i2.dusterchat.profile.ProfileActivity;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityHomeBinding;

import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_GROUPS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;


public class HomeActivity extends AppCompatActivity implements OnItemClickListener {


    private PagerAdapter pagerAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private ActivityHomeBinding binding;
    private DatabaseReference rootRef;

    private String groupName;
    private String uId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        setSupportActionBar(binding.mainToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        binding.tabsPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.tabsPager);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLogin();
        } else {
            verifyUser();
        }
    }

    private void verifyUser() {

        uId = auth.getCurrentUser().getUid();

        rootRef.child(NODE_USERS)
                .child(uId)
                .addValueEventListener(verifyUserEventListener);
    }


    private void sendUserToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void openProfilePage() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void openGroupChatPage(String groupName) {
        Intent groupIntent = new Intent(this, GroupChatActivity.class);
        groupIntent.putExtra(BUNDLE_GROUP_NAME, groupName);
        startActivity(groupIntent);
    }

    private void openFindFriendsPage() {
        Intent friendsIntent = new Intent(this, FindFriendsActivity.class);
        startActivity(friendsIntent);
    }


    private void logOutUser() {
        auth.signOut();
        sendUserToLogin();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_friends:
                openFindFriendsPage();
                return true;

            case R.id.menu_profile:
                openProfilePage();
                return true;

            case R.id.menu_logout:
                logOutUser();
                return true;

            case R.id.menu_groups:
                openGroupDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openGroupDialog() {

        final EditText groupName = new EditText(this);
        groupName.setHint("e.g. High-school friends");

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.GroupDialogStyle);
        builder.setTitle("Enter group name:")
                .setView(groupName)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String name = groupName.getText().toString();
                        if (name.isEmpty()) {
                            Toast.makeText(HomeActivity.this, "Please enter a Group name", Toast.LENGTH_LONG).show();
                        } else {
                            createNewGroup(name);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    private void createNewGroup(final String groupName) {

        rootRef.child(NODE_GROUPS)
                .child(groupName)
                .setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, groupName + " group is successfully created", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public void onItemClick(Bundle bundle) {

        int action = bundle.getInt(CLICK_ACTION);
        switch (action) {

            case ACTION_GROUP_CHAT:
                groupName = bundle.getString(BUNDLE_GROUP_NAME);
                openGroupChatPage(groupName);
                break;
        }
    }

    private ValueEventListener verifyUserEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // if user already setup his profile
            if (!dataSnapshot.child(USERNAME).exists()) {
                openProfilePage();
                Toast.makeText(HomeActivity.this, "Please set up your username", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        if(verifyUserEventListener != null) {
            if(uId != null) {
                rootRef.child(NODE_USERS)
                        .child(uId)
                        .removeEventListener(verifyUserEventListener);
            }
        }
    }

}
