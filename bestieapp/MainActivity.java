package com.linex.bestieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.linex.bestieapp.Message.MessageAdapter;
import com.linex.bestieapp.Message.MessagesList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private final List<MessagesList> messagesLists = new ArrayList<>();
    private String mobile;
    private String email;
    private String name;

   private  int unseenMessage = 0;
    private String lastMessage = "";

    private String chatKey = "";

    private  Boolean dataSet = false;

    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;

    DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://bestiesapp-75777-default-rtdb.firebaseio.com/");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleImageView userProfilePic = (CircleImageView) findViewById(R.id.userProfilePic);


        messageRecyclerView = findViewById(R.id.messageRecyclerView);

        mobile = getIntent().getStringExtra("mobile");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");

        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageAdapter(messagesLists, MainActivity.this);
        messageRecyclerView.setAdapter(messageAdapter);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final String profilePicUrl = snapshot.child("users").child(mobile).child("profile_pic").getValue(String.class);
              /*  if(profilePicUrl != null && !profilePicUrl.isEmpty()){
                   // Picasso.get().load(profilePicUrl).into(userProfilePic);
                    Picasso.get().load(profilePicUrl);
                }*/

                if(profilePicUrl != null && profilePicUrl.isEmpty()){
                    userProfilePic.setImageResource(R.drawable.person);
                }else{
                    Picasso.get()
                            .load(profilePicUrl)
                            .into(userProfilePic);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {progressDialog.dismiss();}
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesLists.clear();
                unseenMessage = 0;
                lastMessage = "";
                chatKey = "";

                for(DataSnapshot dataSnapshot :  snapshot.child("users").getChildren()){

                    final String getMobile = dataSnapshot.getKey();
                    dataSet = false;

                    if(getMobile.equals(mobile)){

                        final String getName = dataSnapshot.child("name").getValue(String.class);
                        final String getProfilePic = dataSnapshot.child("profile_pic").getValue(String.class);


                        databaseReference.child("chat");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")) {

                                            final String getUserOne = dataSnapshot1.child("user_1").child("userId").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").child("userId").getValue(String.class);

                                            if ((Objects.equals(getUserOne, getMobile) && Objects.equals(getUserTwo, mobile)) || (Objects.equals(getUserOne, mobile) && Objects.equals(getUserTwo, getMobile))) {
                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTs(MainActivity.this, getKey));
                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMessageKey > getLastSeenMessage) {
                                                        unseenMessage++;
                                                    }
                                                }
                                            }

                                        }


                                    }
                                }

                                if (!dataSet) {
                                    dataSet = true;

                                    MessagesList messagesList = new MessagesList(getName, getMobile, lastMessage, getProfilePic, unseenMessage, chatKey);
                                    messagesLists.add(messagesList);
                                    messageAdapter.updateData(messagesLists);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClickProfile(View view) {

        startActivity(new Intent(MainActivity.this, Profile.class));
        finish();
    }
}