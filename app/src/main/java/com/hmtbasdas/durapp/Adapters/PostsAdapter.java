package com.hmtbasdas.durapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hmtbasdas.durapp.Models.Admin;
import com.hmtbasdas.durapp.Models.Post;
import com.hmtbasdas.durapp.Models.PostListener;
import com.hmtbasdas.durapp.databinding.PostItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private final ArrayList<Post> posts;
    private final Context context;
    private final PostListener postListener;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    public PostsAdapter(FirebaseAuth auth, ArrayList<Post> posts, Context context, PostListener postListener) {
        this.posts = posts;
        this.context = context;
        this.postListener = postListener;
        this.auth = auth;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding itemBinding = PostItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull  PostsAdapter.MyViewHolder holder, int position) {
        holder.setData(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder {

        PostItemBinding binding;

        public MyViewHolder(@NonNull PostItemBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
            database = FirebaseDatabase.getInstance();
            reference = database.getReference().child("Admins");
        }

        void setData(Post post){

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

            binding.postDate.setText(getReadableDateTime(new Date(post.getDate())));
            binding.postLayout.setBackgroundColor(Color.parseColor("#1C2E46"));

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postListener.onPostClicked(post);
                }
            });
        }
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
    }

}
