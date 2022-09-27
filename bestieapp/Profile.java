package com.linex.bestieapp;

import static com.google.firebase.database.FirebaseDatabase.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class Profile extends AppCompatActivity {

    ImageView imageBox;
    FirebaseDatabase firebaseDatabase ;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageBox = findViewById(R.id.profilePic);

        firebaseDatabase = FirebaseDatabase.getInstance();


        ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();


        FirebaseDatabase firebaseDatabase = getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String profilePicUrl = snapshot.child("users").child("profile_pic").child("images").getValue(String.class);


                if(profilePicUrl != null && !profilePicUrl.isEmpty()){
                    Picasso.get()
                            .load(profilePicUrl)
                            .into(imageBox);
                }else{
                    imageBox.setImageResource(R.drawable.person);
                }
               pd.dismiss();
                    //imageBox.setImageResource(R.drawable.person);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onClickEdit(View view) {
        Toast.makeText(this, "Already Edited", Toast.LENGTH_SHORT).show();

    }

    public void onClickRate(View view) {
        Toast.makeText(this, "Rate is Coming Soon...", Toast.LENGTH_SHORT).show();
    }

    public void onClickShare(View view) {
        Toast.makeText(this, "Shared", Toast.LENGTH_SHORT).show();
    }

    public void onClickLogout(View view) {
        firebaseDatabase.goOffline();
        startActivity(new Intent(Profile.this, Register.class));
        Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
        finish();
        pd.dismiss();

    }

    public void onClickBack(View view) {
        finish();
    }

}