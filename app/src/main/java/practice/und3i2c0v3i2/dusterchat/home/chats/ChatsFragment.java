package practice.und3i2c0v3i2.dusterchat.home.chats;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import practice.und3i2c0v3i2.dusterchat.OnItemClickListener;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ChatsHolderBinding;
import practice.und3i2c0v3i2.dusterchat.databinding.FragmentChatsBinding;
import practice.und3i2c0v3i2.dusterchat.databinding.UsersHolderBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CONTACTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.ACTION_PRIVATE_CHAT;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.CLICK_ACTION;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private OnItemClickListener listener;

    private FragmentChatsBinding binding;
    private DatabaseReference chatsRef;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;

    private String currentUid;
    private FirebaseRecyclerAdapter<User, ViewHolder> adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        currentUid = auth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_CONTACTS)
                .child(currentUid);

        usersRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_USERS);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(chatsRef, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull User model) {

                        final String userID = getRef(position).getKey();
                        usersRef.child(userID)
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        User user = new User();
                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.hasChild(PROFILE_IMG)) {
                                                user.setImgUrl(dataSnapshot.child(PROFILE_IMG).getValue().toString());
                                            }
                                            if (dataSnapshot.hasChild(USERNAME)) {
                                                user.setUsername(dataSnapshot.child(USERNAME).getValue().toString());
                                            }
                                            if (dataSnapshot.hasChild(STATUS)) {
                                                user.setStatus(dataSnapshot.child(STATUS).getValue().toString());
                                            }

                                            holder.holderBinding.setUser(user);

                                            holder.holderBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt(CLICK_ACTION, ACTION_PRIVATE_CHAT);
                                                    bundle.putString(FRIEND_ID, userID);
                                                    bundle.putString(USERNAME, holder.holderBinding.getUser().getUsername());
                                                    bundle.putString(PROFILE_IMG, holder.holderBinding.getUser().getImgUrl());
                                                    listener.onItemClick(bundle);
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        ChatsHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.chats_holder, parent, false);

                        return new ViewHolder(binding);
                    }
                };


        binding.recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ChatsHolderBinding holderBinding;

        public ViewHolder(@NonNull ChatsHolderBinding binding) {
            super(binding.getRoot());
            holderBinding = binding;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        adapter.stopListening();
        binding.recyclerView.setLayoutManager(null);
    }
}
