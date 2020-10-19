package com.fyp.communicationmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactsFragment extends Fragment {

    private View myFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment getInstance(){
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_contacts, container, false);

        viewPager = myFragment.findViewById(R.id.contact_pager);
        tabLayout = myFragment.findViewById(R.id.contact_tabs);

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
        adapter.addFragment(new ContactListFragment(), "List of Contacts");
        adapter.addFragment(new RequestListFragment(), "List of Request");

        viewPager.setAdapter(adapter);
    }
}
