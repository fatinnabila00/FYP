package com.fyp.communicationmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatsFragment extends Fragment {

    private View myFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment getInstance(){
        return new ChatsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_chats, container, false);

        viewPager = myFragment.findViewById(R.id.chat_pager);
        tabLayout = myFragment.findViewById(R.id.chat_tabs);

        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ChatsPrivateFragment(), "Private Chats");
        adapter.addFragment(new ChatsGroupFragment(), "Group Chats");

        viewPager.setAdapter(adapter);
    }
}
