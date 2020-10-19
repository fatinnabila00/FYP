package com.fyp.communicationmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GIFLibraryFragment extends Fragment {

    private View myFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public GIFLibraryFragment() {
        // Required empty public constructor
    }

    public static GIFLibraryFragment getInstance(){
        return new GIFLibraryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_gif_library, container, false);

        viewPager = myFragment.findViewById(R.id.gif_library_pager);
        tabLayout = myFragment.findViewById(R.id.gif_library_tabs);

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
        adapter.addFragment(new MyGIFFragment(), "My GIF");
        adapter.addFragment(new FavouriteGIFFragment(), "Favourite GIF");
        adapter.addFragment(new PendingGIFFragment(), "Pending GIF");

        viewPager.setAdapter(adapter);
    }
}
