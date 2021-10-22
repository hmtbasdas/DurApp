package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hmtbasdas.durapp.Models.Post;
import com.hmtbasdas.durapp.R;
import com.hmtbasdas.durapp.databinding.ActivitySharePostBinding;
import com.hmtbasdas.durapp.databinding.PopupBinding;

import java.util.Date;

public class SharePostActivity extends BaseActivity {

    private ActivitySharePostBinding binding;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private boolean visibility;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Posts");
        auth = FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(this);
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

        binding.sendPostContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlContent()){
                    binding.sendPostContent.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    sendPost();
                }
            }
        });

        binding.switchChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibility = binding.switchChoice.isChecked();
            }
        });

        binding.helpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }

    private void showPopup(){
        final View popupView = getLayoutInflater().inflate(R.layout.popup, null);

        TextView popupText = popupView.findViewById(R.id.popupText);
        Button popupButton = popupView.findViewById(R.id.popupButton);

        popupText.setText(R.string.sharePostTextAlert);
        popupButton.setText(R.string.close);
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendPost(){
        String postID = String.valueOf(System.currentTimeMillis());
        Post post;
        if(visibility){
            post = new Post(auth.getUid(), postID, (new Date()).toString(), binding.editTextProblem.getText().toString(), "","", true, false, true);
        }
        else{
            post = new Post(auth.getUid(), postID, (new Date()).toString(), binding.editTextProblem.getText().toString(), "","", false, false, false);
        }
        reference.child(postID).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Gönderildi", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.sendPostContent.setVisibility(View.VISIBLE);
                onBackPressed();
            }
        });
    }

    private boolean controlContent(){
        if(binding.editTextProblem.getText().toString().length() < 100){
            Toast.makeText(getApplicationContext(), "Lütfen en az 100 karakter kullanın", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }
}