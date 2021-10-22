package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hmtbasdas.durapp.Adapters.PostAdapter;
import com.hmtbasdas.durapp.Models.Post;
import com.hmtbasdas.durapp.Models.PostListener;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.databinding.ActivityMessageBoxBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MessageBoxActivity extends AppCompatActivity implements PostListener {

    private ActivityMessageBoxBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
        getPosts();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Posts");

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(auth,posts, this, this);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(postAdapter);
    }

    private void setListeners(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getPosts(){
        binding.progressBar.setVisibility(View.VISIBLE);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post post = snapshot.getValue(Post.class);
                if(post != null){
                    if(Objects.equals(auth.getUid(), post.getUserID())  && post.isConfirmation() && !post.getReplyAdminContent().isEmpty()){
                        posts.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                checkPostList();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPostClicked(Post post) {
        post.setRead(true);
        reference.child(post.getPostID()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(MessageBoxActivity.this, PostActivity.class);
                    intent.putExtra(Constants.KEY_POST_INTENT, post);
                    intent.putExtra("way","myPosts");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPostList(){
        binding.progressBar.setVisibility(View.INVISIBLE);
        if(!(posts.size() > 0)){
            binding.recyclerView.setVisibility(View.INVISIBLE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }
        else{
            binding.emptyLayout.setVisibility(View.INVISIBLE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }
}