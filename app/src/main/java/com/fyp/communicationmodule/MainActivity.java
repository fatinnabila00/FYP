package com.fyp.communicationmodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private BottomNavigationView bottomNavigationView;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Communication");

        bottomNavigationView = findViewById(R.id.main_tabs);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment = null;

                switch(menuItem.getItemId()){
                    case R.id.chat_menu : selectedFragment = new ChatsFragment(); break;
                    case R.id.library_menu : selectedFragment = new GIFLibraryFragment(); break;
                    case R.id.contact_menu : selectedFragment = new ContactsFragment(); break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, selectedFragment);
                transaction.commit();
                return false;
            }
        });

        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, ChatsFragment.getInstance());
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.menu_communication_module){

        }
        if(item.getItemId()==R.id.menu_add_group){
            RequestNewGroup();
        }
        if(item.getItemId()==R.id.menu_find_friends){
            SendUserToFindFriendsActivity();
        }
        if(item.getItemId()==R.id.menu_communication_module){

        }
        if(item.getItemId()==R.id.menu_assessment_module){

        }
        if(item.getItemId()==R.id.menu_setting){
            SendUserToSettingActivity();
        }
        if(item.getItemId()==R.id.menu_signout){
            mAuth.signOut();
            SendUserToLoginActivity();
        }

        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name : ");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Group A");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please insert group name.", Toast.LENGTH_SHORT);
                }
                else{
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName) {
        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName+" group is created.", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
