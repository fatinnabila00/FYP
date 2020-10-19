package com.fyp.communicationmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsPrivateFragment extends Fragment
{
    private View PrivateChatsView;
    private RecyclerView chatsList;

    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";


    public ChatsPrivateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats_private, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                                                    .setQuery(ChatsRef, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
                protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                {
                    final String usersIDs = getRef(position).getKey();
                    final String[] retImage = {"default_image"};

                    UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                if (dataSnapshot.hasChild("profileImage"))
                                {
                                    retImage[0] = dataSnapshot.child("profileImage").getValue().toString();
                                    Picasso.get().load(retImage[0]).into(holder.profileImage);
                                }

                                final String retName = dataSnapshot.child("userID").getValue().toString();
                                final String retStatus = dataSnapshot.child("fullName").getValue().toString();

                                holder.userName.setText(retName);

                                if (dataSnapshot.child("userState").hasChild("state"))
                                {
                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                    String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                    if (state.equals("online"))
                                    {
                                        holder.userFullName.setText("online");
                                    }
                                    else if (state.equals("offline"))
                                    {
                                        holder.userFullName.setText("Last Seen: " + date + " " + time);
                                    }
                                }
                                else {
                                    holder.userFullName.setText("offline");
                                }

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent chatIntent = new Intent(getContext(), ChatsPrivateActivity.class);
                                        chatIntent.putExtra("visit_user_id", usersIDs);
                                        chatIntent.putExtra("visit_user_name", retName);
                                        chatIntent.putExtra("visit_image", retImage[0]);
                                        startActivity(chatIntent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @NonNull
                @Override
                public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_user_display, viewGroup, false);
                    return new ChatsViewHolder(view);
                }
        };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class  ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView userFullName, userName;

        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userFullName = itemView.findViewById(R.id.user_fullName);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
