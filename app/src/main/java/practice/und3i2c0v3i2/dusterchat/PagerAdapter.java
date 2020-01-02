package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {


    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new ChatsFragment();
                break;

            case 1:
                fragment = new GroupsFragment();
                break;

            case 2:
                fragment = new ContactsFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = "";

        switch (position) {
            case 0:
                title = "Chats";
                break;

            case 1:
                title = "Groups";
                break;

            case 2:
                title = "Contacts";
                break;

        }

        return title;

    }
}
