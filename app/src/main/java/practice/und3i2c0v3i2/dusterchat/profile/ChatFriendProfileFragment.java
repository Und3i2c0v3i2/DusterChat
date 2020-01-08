package practice.und3i2c0v3i2.dusterchat.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import practice.und3i2c0v3i2.dusterchat.OnFragmentInteractionListener;
import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.FragmentChatFriendProfileBinding;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CHAT_REQUESTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_CONTACTS;
import static practice.und3i2c0v3i2.dusterchat.Contract.EMAIL;
import static practice.und3i2c0v3i2.dusterchat.Contract.FACEBOOK;
import static practice.und3i2c0v3i2.dusterchat.Contract.LINKED_IN;
import static practice.und3i2c0v3i2.dusterchat.Contract.PHONE;
import static practice.und3i2c0v3i2.dusterchat.Contract.RECEIVER_UID;
import static practice.und3i2c0v3i2.dusterchat.Contract.REQ_STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_RECEIVED;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS_SENT;
import static practice.und3i2c0v3i2.dusterchat.Contract.TWITTER;
import static practice.und3i2c0v3i2.dusterchat.Contract.CURRENT_UID;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.WEB_PAGE;


public class ChatFriendProfileFragment extends Fragment {

    private FragmentChatFriendProfileBinding binding;
    private OnFragmentInteractionListener listener;

    private DatabaseReference usersRef;
    private DatabaseReference contactsRef;
    private DatabaseReference chatReqRef;

    private DatabaseReference receivedChatReqRef;
    private DatabaseReference sentChatReqRef;

    private DatabaseReference rejectChatReqRef;

    private String receiverUid;
    private String senderUid;
    private User user;

    private String currentReqStatus;


    public ChatFriendProfileFragment() {
        // Required empty public constructor
    }

    public static ChatFriendProfileFragment newInstance(String senderUid, String receiverUid) {
        ChatFriendProfileFragment fragment = new ChatFriendProfileFragment();
        Bundle args = new Bundle();
        args.putString(RECEIVER_UID, receiverUid);
        args.putString(CURRENT_UID, senderUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiverUid = getArguments().getString(RECEIVER_UID);
            senderUid = getArguments().getString(CURRENT_UID);
            user = new User();

            usersRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(NODE_USERS);

            getProfileInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_friend_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setUser(user);

        if(User.ConnStatus.ACCEPTED_REQ.equals(currentReqStatus)) {
            binding.btnSendChatRequest.setText("Remove from contacts");
        } else {
            currentReqStatus = User.ConnStatus.NEW_REQ;
        }

        initializeFirebaseRefs();

        binding.setProfileHandler(this);
    }


    private void initializeFirebaseRefs() {

        chatReqRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_CHAT_REQUESTS);

        contactsRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_CONTACTS);

        initializeFirebaseValueListeners();

        sentChatReqRef = chatReqRef.child(STATUS_SENT)
                .child(senderUid)
                .child(receiverUid);

        receivedChatReqRef = chatReqRef.child(STATUS_RECEIVED)
                .child(receiverUid)
                .child(senderUid);

        rejectChatReqRef = chatReqRef.child(STATUS_SENT)
                .child(receiverUid)
                .child(senderUid);
    }


    private void initializeFirebaseValueListeners() {

        chatReqRef.child(STATUS_RECEIVED)
                .child(senderUid)
                .addValueEventListener(senderReceivedReq);

        chatReqRef.child(STATUS_RECEIVED)
                .child(receiverUid)
                .addValueEventListener(receiverReceivedReq);

        contactsRef.child(receiverUid)
                .addValueEventListener(contactsListener);
    }


    public void getProfileInfo() {

        usersRef.child(receiverUid)
                .addValueEventListener(friendProfileEventListener);

    }

    public void handleChatRequest() {

        if (currentReqStatus.equals(User.ConnStatus.NEW_REQ)) {
            sendChatRequest();
        } else if (currentReqStatus.equals(User.ConnStatus.RECEIVED_REQ)) {
            cancelChatRequest();
        } else if (currentReqStatus.equals(User.ConnStatus.ACCEPTED_REQ)) {
//            acceptChatRequest();
        }
    }

    public void sendChatRequest() {
        binding.btnSendChatRequest.setEnabled(false);
        binding.btnSendChatRequest.setTextColor(getResources().getColor(R.color.colorGrey));

        sentChatReqRef.child(REQ_STATUS)
                .setValue(User.ConnStatus.SENT_REQ)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            receivedChatReqRef.child(REQ_STATUS)
                                    .setValue(User.ConnStatus.RECEIVED_REQ)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.btnSendChatRequest.setEnabled(true);
                                                binding.btnSendChatRequest.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                                binding.btnSendChatRequest.setText((getResources().getString(R.string.btn_cancel_chat_request)));

                                                currentReqStatus = User.ConnStatus.RECEIVED_REQ;
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void cancelChatRequest() {

        sentChatReqRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            receivedChatReqRef.removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.btnSendChatRequest.setEnabled(true);
                                                binding.btnSendChatRequest.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                                binding.btnSendChatRequest.setText((getResources().getString(R.string.btn_send_chat_request)));
                                                currentReqStatus = User.ConnStatus.NEW_REQ;
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    public void acceptChatRequest() {

        contactsRef.child(senderUid)
                .child(receiverUid)
                .child(NODE_CONTACTS)
                .setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            contactsRef.child(receiverUid)
                                    .child(senderUid)
                                    .child(NODE_CONTACTS)
                                    .setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                chatReqRef.child(STATUS_SENT)
                                                        .child(receiverUid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatReqRef.child(STATUS_RECEIVED)
                                                                            .child(senderUid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        binding.btnAcceptChatRequest.setVisibility(View.INVISIBLE);
                                                                                        binding.btnRejectChatRequest.setVisibility(View.INVISIBLE);
                                                                                        binding.btnSendChatRequest.setText("Remove from contacts");
                                                                                        currentReqStatus = User.ConnStatus.ACCEPTED_REQ;
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

    public void rejectChatRequest() {

        rejectChatReqRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatReqRef.child(STATUS_RECEIVED)
                                    .child(senderUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.btnAcceptChatRequest.setVisibility(View.INVISIBLE);
                                                binding.btnRejectChatRequest.setVisibility(View.INVISIBLE);
                                                currentReqStatus = User.ConnStatus.NEW_REQ;
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private ValueEventListener friendProfileEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists() && dataSnapshot.hasChild(USERNAME)) {
                user.setUsername(dataSnapshot.child(USERNAME).getValue().toString());
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

            binding.setUser(user);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    private ValueEventListener receiverReceivedReq = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild(senderUid)) {

                String reqStatus = dataSnapshot
                        .child(senderUid)
                        .child(REQ_STATUS)
                        .getValue().toString();


                if (reqStatus.equals(User.ConnStatus.RECEIVED_REQ)) {
                    binding.btnSendChatRequest.setText((getResources().getString(R.string.btn_cancel_chat_request)));
                }
            } else {
                binding.btnSendChatRequest.setText((getResources().getString(R.string.btn_send_chat_request)));
                currentReqStatus = User.ConnStatus.NEW_REQ;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener senderReceivedReq = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild(receiverUid)) {
                String reqStatus = dataSnapshot
                        .child(receiverUid)
                        .child(REQ_STATUS)
                        .getValue().toString();


                if (reqStatus.equals(User.ConnStatus.RECEIVED_REQ)) {
                    binding.btnAcceptChatRequest.setVisibility(View.VISIBLE);
                    binding.btnRejectChatRequest.setVisibility(View.VISIBLE);
                }
            } else {
                binding.btnAcceptChatRequest.setVisibility(View.INVISIBLE);
                binding.btnRejectChatRequest.setVisibility(View.INVISIBLE);
                currentReqStatus = User.ConnStatus.NEW_REQ;

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener contactsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.hasChild(senderUid)) {
                currentReqStatus = User.ConnStatus.ACCEPTED_REQ;
                binding.btnSendChatRequest.setText("Remove from contacts");

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;

        if (receiverReceivedReq != null) {
            chatReqRef.child(STATUS_RECEIVED)
                    .child(receiverUid)
                    .removeEventListener(receiverReceivedReq);
        }

        if (senderReceivedReq != null) {
            chatReqRef.child(STATUS_RECEIVED)
                    .child(senderUid)
                    .removeEventListener(senderReceivedReq);
        }

        if (friendProfileEventListener != null) {
            usersRef.child(receiverUid)
                    .removeEventListener(friendProfileEventListener);
        }

        if (contactsListener != null) {
            contactsRef.child(receiverUid)
                    .removeEventListener(contactsListener);
        }
    }
}
