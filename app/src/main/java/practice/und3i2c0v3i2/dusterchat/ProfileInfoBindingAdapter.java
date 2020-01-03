package practice.und3i2c0v3i2.dusterchat;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import static practice.und3i2c0v3i2.dusterchat.Contract.EMAIL;
import static practice.und3i2c0v3i2.dusterchat.Contract.PHONE;
import static practice.und3i2c0v3i2.dusterchat.Contract.STATUS;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERNAME;
import static practice.und3i2c0v3i2.dusterchat.Contract.USERS;
import static practice.und3i2c0v3i2.dusterchat.Contract.WEB_PAGE;

public class ProfileInfoBindingAdapter {


    private static String username;
    private static String status;
    private static String phone;
    private static String email;
    private static String webPage;

    @BindingAdapter({"profile_username_rootRef", "profile_username_uid"})
    public static void setUsername(EditText view, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()
                                && dataSnapshot.hasChild(USERNAME)) {
                            username = dataSnapshot.child(USERNAME).getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.setText(username);
    }

    @BindingAdapter({"profile_status_rootRef", "profile_status_uid"})
    public static void setStatus(EditText view, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()
                                && dataSnapshot.hasChild(STATUS)) {
                            status = dataSnapshot.child(STATUS).getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.setText(status);
    }

    @BindingAdapter({"profile_phone_rootRef", "profile_phone_uid"})
    public static void setPhone(EditText view, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()
                                && dataSnapshot.hasChild(PHONE)) {
                            phone = dataSnapshot.child(PHONE).getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.setText(phone);
    }

    @BindingAdapter({"profile_email_rootRef", "profile_email_uid"})
    public static void setEmail(EditText view, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()
                                && dataSnapshot.hasChild(EMAIL)) {
                            email = dataSnapshot.child(EMAIL).getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.setText(email);
    }

    @BindingAdapter({"profile_web_rootRef", "profile_web_uid"})
    public static void setWebPage(EditText view, DatabaseReference rootRef, String uID) {

        rootRef.child(USERS)
                .child(uID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()
                                && dataSnapshot.hasChild(WEB_PAGE)) {
                            webPage = dataSnapshot.child(WEB_PAGE).getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.setText(webPage);
    }

//    @androidx.databinding.BindingAdapter("loadImg")
//    public static void bindImg(ImageView view, String imgUrl) {
//        Picasso.get()
//                .load(imgUrl)
//                .fit()
//                .into(view);
//    }
}
