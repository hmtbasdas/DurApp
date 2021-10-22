package com.hmtbasdas.durapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hmtbasdas.durapp.Models.Admin;
import com.hmtbasdas.durapp.Models.Post;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.databinding.ActivityPostBinding;

public class PostActivity extends BaseActivity {

    private ActivityPostBinding binding;
    private Post post;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String postWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
        setPostValues();
    }

    private void init(){
        post = (Post) getIntent().getExtras().get(Constants.KEY_POST_INTENT);
        postWay = (String) getIntent().getExtras().get("way");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Admins");
    }

    private void setListeners(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(postWay.equals("myPosts")){
                    intent = new Intent(getApplicationContext(), MessageBoxActivity.class);
                }
                else{
                    intent = new Intent(getApplicationContext(), PostsActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(postWay.equals("myPosts")){
            intent = new Intent(getApplicationContext(), MessageBoxActivity.class);
        }
        else{
            intent = new Intent(getApplicationContext(), PostsActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void setPostValues(){
        binding.noticeContent1.setText(post.getContent());

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Admin admin = snapshot.getValue(Admin.class);
                if(admin != null){
                    if(admin.getId().equals(post.getReplyAdminID())){
                        binding.adminName.setText(admin.getName() + " " + admin.getSurname());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.noticeContent2.setText(post.getReplyAdminContent());
    }
}