package practice.und3i2c0v3i2.dusterchat;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import practice.und3i2c0v3i2.dusterchat.databinding.FragmentGroupsBinding;

import static practice.und3i2c0v3i2.dusterchat.Contract.GROUPS;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private DatabaseReference groupRef;
    private List<String> list;
    private GroupsAdapter adapter;
    private OnItemClickListener listener;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups, container, false);


        groupRef = FirebaseDatabase.getInstance()
                .getReference().child(GROUPS);

        list = new ArrayList<>();

        displayGroups();
        setupRecycler();

        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new GroupsAdapter(list, listener);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
    }

    private void displayGroups() {
        groupRef.addValueEventListener(groupsEventListener);
    }

    private ValueEventListener groupsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Set<String> set = new HashSet<>();
            Iterator iterator = dataSnapshot.getChildren().iterator();

            while ((iterator.hasNext())) {
                set.add(((DataSnapshot)iterator.next()).getKey());
            }

            list.clear();
            list.addAll(set);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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
        if(listener != null) {
            listener = null;
        }

        if(groupsEventListener != null) {
            groupRef.removeEventListener(groupsEventListener);
        }

    }

}
