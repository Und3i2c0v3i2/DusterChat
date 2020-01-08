package practice.und3i2c0v3i2.dusterchat.find_friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import practice.und3i2c0v3i2.dusterchat.model.User;
import practice.und3i2c0v3i2.dusterchat.profile.ChatProfileActivity;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityFindFriendsBinding;
import practice.und3i2c0v3i2.dusterchat.databinding.UsersHolderBinding;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;

public class FindFriendsActivity extends AppCompatActivity {


    private ActivityFindFriendsBinding friendsBinding;
    private DatabaseReference usersRef;
    private FirebaseRecyclerAdapter<User, FindFriendsViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsBinding = DataBindingUtil.setContentView(this, R.layout.activity_find_friends);

        usersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_USERS);


        setSupportActionBar(friendsBinding.appbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_friends));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        friendsBinding.friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> firebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(usersRef, User.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<User, FindFriendsViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, final int position, @NonNull User model) {

                        holder.binding.setUser(model);

                        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uidKey = getRef(holder.getAdapterPosition()).getKey();
                                Intent profileIntent = new Intent(FindFriendsActivity.this, ChatProfileActivity.class);
                                profileIntent.putExtra(FRIEND_ID, uidKey);
                                startActivity(profileIntent);
                            }
                        });


                    }


                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        UsersHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.users_holder, parent, false);

                        return new FindFriendsViewHolder(binding);
                    }
                };


        friendsBinding.friendsRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        friendsBinding.friendsRecyclerView.setLayoutManager(null);
    }

    public class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        private UsersHolderBinding binding;

        public FindFriendsViewHolder(@NonNull UsersHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
