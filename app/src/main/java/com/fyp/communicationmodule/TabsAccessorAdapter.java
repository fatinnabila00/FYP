package com.fyp.communicationmodule;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0  : ChatsFragment chatsFragment = new ChatsFragment();
                      return chatsFragment;
            case 1  : GIFLibraryFragment gifLibraryFragment = new GIFLibraryFragment();
                      return gifLibraryFragment;
            case 2  : ContactsFragment contactsFragment = new ContactsFragment();
                      return contactsFragment;
            default : return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0  : return "Chats";
            case 1  : return "GIF Library";
            case 2  : return "Contacts";
            default : return null;
        }
    }
}
