package practice.und3i2c0v3i2.dusterchat;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;

public class ProfileInfoBindingAdapter {

    private static String username;
    private static String status;

    @BindingAdapter({"profile_username_rootRef", "profile_username_uid"})
    public static void getUsername(EditText et, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()
                                && dataSnapshot.hasChild(USERNAME)) {
                            username = dataSnapshot.child(USERNAME).getValue().toString();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        et.setText(username);
    }

    @BindingAdapter({"profile_status_rootRef", "profile_status_uid"})
    public static void getStatus(EditText et, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()
                                && dataSnapshot.hasChild(STATUS)) {
                            status = dataSnapshot.child(STATUS).getValue().toString();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        et.setText(status);
    }

//    @androidx.databinding.BindingAdapter("loadImg")
//    public static void bindImg(ImageView view, String imgUrl) {
//        Picasso.get()
//                .load(imgUrl)
//                .fit()
//                .into(view);
//    }
}
