package practice.und3i2c0v3i2.dusterchat.home.requests;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import practice.und3i2c0v3i2.dusterchat.OnItemClickListener;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.FragmentRequestsBinding;
import practice.und3i2c0v3i2.dusterchat.databinding.ReqHolderBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.FRIEND_ID;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CHAT_REQUESTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CONTACTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_RECEIVED;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_SENT;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.ACTION_OPEN_PROFILE;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.CLICK_ACTION;
import static practice.und3i2c0v3i2.dusterchat.model.User.ConnStatus.ACCEPTED_REQ;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private OnItemClickListener listener;

    private FragmentRequestsBinding requestsBinding;
    private DatabaseReference currentUserChatReqRef;
    private DatabaseReference chatReqRef;
    private DatabaseReference usersRef;
    private DatabaseReference contactsRef;
    private FirebaseAuth auth;
    private String currentUserId;

    private FirebaseRecyclerAdapter<User, RequestsFragment.ViewHolder> adapter;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requestsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_requests, container, false);
        requestsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        chatReqRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_CHAT_REQUESTS);

        currentUserChatReqRef = chatReqRef
                .child(STATUS_RECEIVED)
                .child(currentUserId);

        usersRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_USERS);

        contactsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(NODE_CONTACTS);

        return requestsBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(currentUserChatReqRef, User.class)
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
                                if (dataSnapshot.hasChild(PROFILE_IMG)) {
                                    user.setImgUrl(dataSnapshot.child(PROFILE_IMG).getValue().toString());
                                }
                                if (dataSnapshot.hasChild(USERNAME)) {
                                    user.setUsername(dataSnapshot.child(USERNAME).getValue().toString());
                                }
                                if (dataSnapshot.hasChild(STATUS)) {
                                    user.setStatus(dataSnapshot.child(STATUS).getValue().toString());
                                }

                                holder.binding.setUser(user);
                                holder.binding.usersUsername.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        goToProfile(userID);
                                    }
                                });
                                holder.binding.usersProfileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        goToProfile(userID);
                                    }
                                });

                                holder.binding.btnAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        acceptChatRequest(userID);
                                    }
                                });

                                holder.binding.btnDecline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        rejectChatRequest(userID);
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
                ReqHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.req_holder, parent, false);

                return new ViewHolder(binding);
            }
        };

        requestsBinding.recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void rejectChatRequest(final String uid) {

        chatReqRef.child(STATUS_RECEIVED)
                .child(currentUserId)
                .child(uid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatReqRef.child(STATUS_SENT)
                                    .child(uid)
                                    .removeValue();
                        }
                    }
                });

    }

    public void acceptChatRequest(final String uid) {

        contactsRef.child(currentUserId)
                .child(uid)
                .child(NODE_CONTACTS)
                .setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            contactsRef.child(uid)
                                    .child(currentUserId)
                                    .child(NODE_CONTACTS)
                                    .setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                chatReqRef.child(STATUS_SENT)
                                                        .child(uid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatReqRef.child(STATUS_RECEIVED)
                                                                            .child(currentUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
//                                                                                        binding.btnAcceptChatRequest.setVisibility(View.INVISIBLE);
//                                                                                        binding.btnRejectChatRequest.setVisibility(View.INVISIBLE);
//                                                                                        binding.btnSendChatRequest.setText("Remove from contacts");
////                                                                                        currentReqStatus = ACCEPTED_REQ;
//                                                                                        currentContactsStatus = ACCEPTED_REQ;
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void goToProfile(String id) {
        Bundle bundle = new Bundle();
        bundle.putInt(CLICK_ACTION, ACTION_OPEN_PROFILE);
        bundle.putString(FRIEND_ID, id);
        listener.onItemClick(bundle);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        adapter.stopListening();
        requestsBinding.recyclerView.setLayoutManager(null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ReqHolderBinding binding;

        public ViewHolder(@NonNull ReqHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
