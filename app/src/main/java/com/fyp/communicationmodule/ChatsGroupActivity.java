package com.fyp.communicationmodule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ChatsGroupActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText messageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, GroupNameRef, GroupMessageKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_group);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        InitializeFields();
        GetUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageInfoToDatabase();
                messageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = (ImageButton) findViewById(R.id.sendGroupMessage);

        messageInput = (EditText) findViewById(R.id.inputGroupMessage);
        mScrollView = (ScrollView) findViewById(R.id.chat_scroll_view);
        displayTextMessages = (TextView) findViewById(R.id.groupChatText);
    }

    private void GetUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("fullName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatSenderName = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatSenderName+"\n"+chatMessage+"\n"+chatDate+", "+chatTime+"\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void SendMessageInfoToDatabase() {
        String message = messageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please write your message.", Toast.LENGTH_SHORT);
        }
        else{
            Calendar date = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            currentDate = dateFormat.format(date.getTime());

            Calendar time = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            currentTime = timeFormat.format(time.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfo = new HashMap<>();
            messageInfo.put("fullName", currentUserName);
            messageInfo.put("message", message);
            messageInfo.put("date", currentDate);
            messageInfo.put("time", currentTime);

            GroupMessageKeyRef.updateChildren(messageInfo);

        }
    }
}
