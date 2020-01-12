package practice.und3i2c0v3i2.dusterchat.home.contacts;


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
import practice.und3i2c0v3i2.dusterchat.databinding.ContactsHolderBinding;
import practice.und3i2c0v3i2.dusterchat.databinding.FragmentContactsBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CONTACTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.ACTION_OPEN_PROFILE;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.ACTION_PRIVATE_CHAT;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.CLICK_ACTION;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private OnItemClickListener listener;

    private FragmentContactsBinding contactsBinding;
    private DatabaseReference contactsRef;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;
    private String currentUserId;

    private FirebaseRecyclerAdapter<User, ContactsFragment.ViewHolder> adapter;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);
        contactsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_CONTACTS)
                .child(currentUserId);

        usersRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_USERS);

        return contactsBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(contactsRef, User.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final User model) {

                        final String userID = getRef(position).getKey();
                        usersRef.child(userID)
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        User user = new User();
                                        if(dataSnapshot.hasChild(PROFILE_IMG)) {
                                           user.setImgUrl(dataSnapshot.child(PROFILE_IMG).getValue().toString());
                                        }
                                        if(dataSnapshot.hasChild(USERNAME)) {
                                            user.setUsername(dataSnapshot.child(USERNAME).getValue().toString());
                                        }
                                        if(dataSnapshot.hasChild(STATUS)) {
                                            user.setStatus(dataSnapshot.child(STATUS).getValue().toString());
                                        }

                                        holder.binding.setUser(user);

                                        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putInt(CLICK_ACTION, ACTION_OPEN_PROFILE);
                                                bundle.putString(FRIEND_ID, userID);
                                                listener.onItemClick(bundle);
                                            }
                                        });

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
                        ContactsHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.contacts_holder, parent, false);

                        return new ViewHolder(binding);
                    }
                };

        contactsBinding.recyclerView.setAdapter(adapter);
        adapter.startListening();
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
        adapter.stopListening();
        contactsBinding.recyclerView.setLayoutManager(null);
        listener = null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ContactsHolderBinding binding;

        public ViewHolder(@NonNull ContactsHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
