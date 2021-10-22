package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hmtbasdas.durapp.Adapters.NoticeAdapter;
import com.hmtbasdas.durapp.Models.Notice;
import com.hmtbasdas.durapp.Models.NoticeListener;
import com.hmtbasdas.durapp.databinding.ActivityNoticeBinding;

import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity implements NoticeListener {

    private ActivityNoticeBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private NoticeAdapter noticeAdapter;
    private ArrayList<Notice> notices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
        getNotices();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Notices");

        notices = new ArrayList<>();

        noticeAdapter = new NoticeAdapter(this, notices, this);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(noticeAdapter);
    }

    private void setListeners(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getNotices(){
        binding.progressBar.setVisibility(View.VISIBLE);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notice notice = snapshot.getValue(Notice.class);
                if(notice != null){
                    if(notice.getStatus()){
                        notices.add(notice);
                    }
                }
                noticeAdapter.notifyDataSetChanged();
                checkNoticeList();
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

    private void checkNoticeList(){
        binding.progressBar.setVisibility(View.INVISIBLE);
        if(!(notices.size() > 0)){
            binding.recyclerView.setVisibility(View.INVISIBLE);
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }
        else{
            binding.emptyLayout.setVisibility(View.INVISIBLE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoticeClicked(Notice notice) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(notice.getLink()));
        startActivity(intent);
    }
}