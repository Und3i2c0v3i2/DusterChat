package practice.und3i2c0v3i2.dusterchat.home.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.PrivateChatDisplayMsgLayoutBinding;
import practice.und3i2c0v3i2.dusterchat.model.Message;
import practice.und3i2c0v3i2.dusterchat.model.User;

import static practice.und3i2c0v3i2.dusterchat.Contract.NODE_USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.PROFILE_IMG;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MessageHolder> {


    private List<Message> list;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    private String senderId;

    public ChatsAdapter(List<Message> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PrivateChatDisplayMsgLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.private_chat_display_msg_layout, parent, false);

        auth = FirebaseAuth.getInstance();


        return new MessageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder holder, int position) {

        senderId = auth.getCurrentUser().getUid();
        final Message message = list.get(position);

        final String fromId = message.getFrom();
        String type = message.getType();

        final User user = new User();

        usersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_USERS)
                .child(fromId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(PROFILE_IMG)) {
                    String imgUrl = dataSnapshot.child(PROFILE_IMG).getValue().toString();
                    user.setImgUrl(imgUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.binding.setUser(user);
        holder.binding.setMsg(message);

        if ("text".equals(type)) {
            holder.binding.receiverLinearLayout.setVisibility(View.INVISIBLE);
            if (senderId.equals(fromId)) {
                holder.binding.senderLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.binding.receiverLinearLayout.setVisibility(View.VISIBLE);
                holder.binding.senderLinearLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MessageHolder extends RecyclerView.ViewHolder {

        PrivateChatDisplayMsgLayoutBinding binding;

        public MessageHolder(PrivateChatDisplayMsgLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }

}
