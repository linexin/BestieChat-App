package com.linex.bestieapp.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.linex.bestieapp.MemoryData;
import com.linex.bestieapp.R;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://bestiesapp-75777-default-rtdb.firebaseio.com/");

    private final List<ChatList> chatLists = new ArrayList<>();

    private String chatKey;
    String getUserMobile = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime =  true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        final TextView nameTv = findViewById(R.id.name);
        final EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
        final CircleImageView profilePic = (CircleImageView) findViewById(R.id.profilePic);
        final ImageView sendBtn = (ImageView) findViewById(R.id.sendBtn);

        chattingRecyclerView = (RecyclerView) findViewById(R.id.chattingRecyclerView);

        final String getName = getIntent().getStringExtra("name");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");

        getUserMobile = MemoryData.getData(Chat.this);

        //nameTv.setText(getName);
        //when ur update profile button then you can use it
        //Picasso.get().load(getProfilePic).into(profilePic);

        nameTv.setText(getName);

        if(getProfilePic.isEmpty()){
            profilePic.setImageResource(R.drawable.person);
        }else{
            Picasso.get()
                    .load(getProfilePic)
                    .into(profilePic);
        }


        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if (chatKey.isEmpty()) {
                    chatKey = "1";

                    if (snapshot.hasChild("chat")) {
                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                    }
                }

                if(snapshot.hasChild("chat")){

                    if(snapshot.child("chat").child(chatKey).hasChild("messages")  ){

                        chatLists.clear();

                        for(DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()){

                            if(messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("mobile") ){
                                final String messageTimestamps  = messagesSnapshot.getKey();
                                final String getMobile = messagesSnapshot.child("mobile").getValue(String.class);
                                final String getMsg = messagesSnapshot.child("msg").getValue(String.class);

                                Timestamp timestamp = new Timestamp(Long.parseLong(messageTimestamps));
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("aa-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh mm aa", Locale.getDefault());
                                ChatList chatList = new ChatList(getMobile, getName, getMsg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                chatLists.add(chatList);

                                if(loadingFirstTime || Long.parseLong(messageTimestamps) > Long.parseLong(MemoryData.getLastMsgTs(Chat.this, chatKey)) ){

                                    loadingFirstTime = false;

                                    MemoryData.saveLastMsgTs(messageTimestamps, chatKey, Chat.this);
                                    chatAdapter.updateChatList(chatLists);

                                    chattingRecyclerView.scrollToPosition(chatLists.size()  - 1);
                                }
                            }


                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String getTxtMessage = messageEditText.getText().toString();

                final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);



                databaseReference.child("chat").child(chatKey).child("user_1").child(getUserMobile).setValue(getUserMobile);
                databaseReference.child("chat").child(chatKey).child("user_2").child(getUserMobile).setValue(getMobile);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(getTxtMessage);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("mobile").setValue(getUserMobile);


                messageEditText.setText("");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}