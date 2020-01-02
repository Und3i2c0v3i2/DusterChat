package practice.und3i2c0v3i2.dusterchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import practice.und3i2c0v3i2.dusterchat.databinding.GroupHolderBinding;

import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.ACTION_GROUP_CHAT;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.BUNDLE_GROUP_NAME;
import static practice.und3i2c0v3i2.dusterchat.OnItemClickListener.CLICK_ACTION;

public class GroupsAdapter extends RecyclerView.Adapter <GroupsAdapter.GroupHolder> {


    private List<String> list;
    private OnItemClickListener listener;

    public GroupsAdapter(List<String> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GroupHolderBinding binding = DataBindingUtil.inflate(inflater, R.layout.group_holder, parent, false);
        return new GroupHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {

        holder.binding.groupName.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class GroupHolder extends RecyclerView.ViewHolder {

        GroupHolderBinding binding;

        public GroupHolder(GroupHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.getRoot()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(CLICK_ACTION, ACTION_GROUP_CHAT);
                            bundle.putString(BUNDLE_GROUP_NAME, list.get(getAdapterPosition()));
                            listener.onItemClick(bundle);
                        }
                    });
        }


    }

}
