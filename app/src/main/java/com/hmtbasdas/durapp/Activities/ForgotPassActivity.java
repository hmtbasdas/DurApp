package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hmtbasdas.durapp.databinding.ActivityForgotPassBinding;

public class ForgotPassActivity extends BaseActivity  {

    private ActivityForgotPassBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
    }

    private void setListeners(){
        binding.sendResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVerifiedEmail()){
                    binding.sendResetPass.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    sendReset(binding.inputEmail.getText().toString());
                }
            }
        });
    }

    private void ToastMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendReset(String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ToastMessage("Başarılı");
                            binding.inputEmail.getText().clear();
                            onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastMessage("Hesap Bulunamadı!");
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        binding.sendResetPass.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean isVerifiedEmail(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            ToastMessage("Mail Adresinizi Giriniz");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            ToastMessage("Geçersiz Mail Adresi");
            return false;
        }
        else {
            return true;
        }
    }
}