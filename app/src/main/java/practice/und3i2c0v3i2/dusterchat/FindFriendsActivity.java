package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityFindFriendsBinding;
import practice.und3i2c0v3i2.dusterchat.model.Contacts;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;

public class FindFriendsActivity extends AppCompatActivity {


    private ActivityFindFriendsBinding binding;
    private DatabaseReference usersRef;
    private FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_friends);

        usersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS);

        setSupportActionBar(binding.appbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_friends));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(usersRef, Contacts.class)
                .build();

        adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {


                        holder.username.setText(model.getUsername());
                        holder.status.setText(model.getStatus());

                        Picasso.get()
                                .load(model.getProfile_img())
                                .placeholder(R.drawable.ic_profile_img_holder)
                                .into(holder.profileImg);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uidKey = getRef(position).getKey();
                                Log.d("TAG", uidKey);
                                Intent profileIntent = new Intent(FindFriendsActivity.this, FriendProfileActivity.class);
                                profileIntent.putExtra(FRIEND_ID, uidKey);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.users_holder, parent, false);

                        return new FindFriendsViewHolder(view);
                    }
                };


        binding.friendsRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView username, status;
        CircleImageView profileImg;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.users_username);
            status = itemView.findViewById(R.id.users_status);
            profileImg = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
