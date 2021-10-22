package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hmtbasdas.durapp.Models.AESCrypt;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.databinding.ActivitySignUpBinding;

import java.util.Date;
import java.util.HashMap;


public class SignUpActivity extends BaseActivity  {

    private FirebaseAuth auth;
    private ActivitySignUpBinding binding;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void ToastMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        binding.textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignUpDetails()){

                    binding.buttonSignUp.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    signup(binding.inputEmail.getText().toString(), binding.inputPass.getText().toString());
                }
            }
        });
    }

    private boolean isValidSignUpDetails(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            ToastMessage("Adınızı Girin");
            return false;
        }
        else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            ToastMessage("Mail Adresinizi Girin");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            ToastMessage("Geçersiz Mail Adresi");
            return false;
        }
        else if(binding.inputPass.getText().toString().trim().isEmpty()){
            ToastMessage("Şifrenizi Girin");
            return false;
        }
        else if(binding.inputPass.getText().toString().trim().length() < 6){
            ToastMessage("Şifreniz En Az 6 Karakter İçermeli");
            return false;
        }
        else if(binding.inputConfirmPass.getText().toString().trim().isEmpty()){
            ToastMessage("Şifrenizi Doğrulayın");
            return false;
        }
        else if(!binding.inputPass.getText().toString().equals(binding.inputConfirmPass.getText().toString())){
            ToastMessage("Şifreler Aynı Olmalı");
            return false;
        }
        else {
            return true;
        }
    }

    private void signup(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            addUser();

                            binding.inputName.getText().clear();
                            binding.inputPass.getText().clear();
                            binding.inputEmail.getText().clear();
                            binding.inputConfirmPass.getText().clear();

                            onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.INVISIBLE);
                        binding.buttonSignUp.setVisibility(View.VISIBLE);

                        ToastMessage(e.getMessage());
                    }
                });
    }

    private void addUser(){
        HashMap<String, Object> user = new HashMap<>();

        try {
            user.put(Constants.KEY_USER_NAME, binding.inputName.getText().toString());
            user.put(Constants.KEY_USER_PASS, AESCrypt.encrypt(binding.inputPass.getText().toString()));
            user.put(Constants.KEY_USER_MAIL, binding.inputEmail.getText().toString());
            user.put(Constants.KEY_USER_CREATE_DATE, new Date());

            database.collection(Constants.KEY_USER_COLLECTION)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            ToastMessage("Kayıt Başarılı");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ToastMessage("Bir hata oluştu tekrar deneyin");
                        }
                    });
        }
        catch (Exception e) {
            ToastMessage("Bir hata oluştu tekrar deneyin");
        }
    }
}