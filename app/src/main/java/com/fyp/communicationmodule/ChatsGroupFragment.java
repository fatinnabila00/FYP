package com.fyp.communicationmodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsGroupFragment extends Fragment {

    private View groupFragmentView;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupList = new ArrayList<>();

    private DatabaseReference RootRef;

    public ChatsGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_chats_group, container, false);

        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeFields();
        RetrieveAndDisplayGroup();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(), ChatsGroupActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);
            }
        });

//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RequestNewGroup();
//            }
//        });

        return groupFragmentView;
    }

    private void InitializeFields() {
        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
//        floatingActionButton = (FloatingActionButton) groupFragmentView.findViewById((R.id.menu_add_group2));
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groupList);
        listView.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroup() {
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                groupList.clear();
                groupList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void RequestNewGroup() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
//        builder.setTitle("Enter group name : ");
//        final EditText groupNameField = new EditText(getActivity());
//        groupNameField.setHint("e.g Group A");
//        builder.setView(groupNameField);
//
//        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String groupName = groupNameField.getText().toString();
//
//                if(TextUtils.isEmpty(groupName)){
//                    Toast.makeText(getActivity(), "Please insert group name.", Toast.LENGTH_SHORT);
//                }
//                else{
//                    CreateNewGroup(groupName);
//                }
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//    private void CreateNewGroup(final String groupName) {
//        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(getActivity(), groupName+" group is created.", Toast.LENGTH_SHORT);
//                }
//            }
//        });
//    }

}
