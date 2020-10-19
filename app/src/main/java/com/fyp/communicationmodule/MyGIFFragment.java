package com.fyp.communicationmodule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyGIFFragment extends Fragment {

    private View GIFView;
    private RecyclerView myGIFList;
    private SearchView searchView;

    private DatabaseReference GIFRef;
    private FirebaseAuth mAuth;

    ArrayList<GIF> list;

    public MyGIFFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GIFView = inflater.inflate(R.layout.fragment_my_gif, container, false);
        myGIFList = (RecyclerView) GIFView.findViewById(R.id.gif_list);
        searchView = (SearchView) GIFView.findViewById(R.id.search_bar);

        mAuth = FirebaseAuth.getInstance();

        GIFRef = FirebaseDatabase.getInstance().getReference().child("SignLanguageGIF");

        return GIFView;
    }

    @Override
    public void onStart() {

        super.onStart();

        if(GIFRef != null){
            GIFRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        list = new ArrayList<>();

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            list.add(ds.getValue(GIF.class));
                        }

                        GIFAdapter gifAdapter = new GIFAdapter(list);
                        myGIFList.setLayoutManager(new GridLayoutManager(getActivity(),2));
                        myGIFList.setAdapter(gifAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return true;
                }
            });
        }
    }

    private void search(String s) {
        ArrayList<GIF> myList = new ArrayList<>();

        for(GIF gif : list){
            if(gif.getEngCaption().toLowerCase().contains(s.toLowerCase()) || gif.getMalayCaption().toLowerCase().contains(s.toLowerCase())){
                myList.add(gif);
            }
        }

        GIFAdapter gifAdapter = new GIFAdapter(myList);
        myGIFList.setAdapter(gifAdapter);
    }
}