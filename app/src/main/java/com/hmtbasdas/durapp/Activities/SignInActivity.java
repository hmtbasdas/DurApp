package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hmtbasdas.durapp.Models.AESCrypt;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.Utilities.PreferenceManager;
import com.hmtbasdas.durapp.databinding.ActivitySignInBinding;

import java.util.Date;
import java.util.HashMap;

public class SignInActivity extends BaseActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth auth;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
    }

    private void ToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListeners() {
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidSignInDetails()) {
                    binding.buttonSignIn.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    try {
                        login(binding.inputEmail.getText().toString(), binding.inputPass.getText().toString());
                    } catch (Exception e) {
                        ToastMessage("Bir hata oluştu tekrar deneyiniz");
                    }
                }
            }
        });

        binding.textCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new SignUpActivity());
            }
        });

        binding.forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new ForgotPassActivity());
            }
        });
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserValues(email, password);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        binding.buttonSignIn.setVisibility(View.VISIBLE);

                        ToastMessage("Mail adresinizi ve şifrenizi kontrol edin!");
                    }
                });
    }

    private boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            ToastMessage("Mail Adresinizi Girin");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            ToastMessage("Geçersiz Mail Adresi");
            return false;
        } else if (binding.inputPass.getText().toString().trim().isEmpty()) {
            ToastMessage("Şifrenizi Girin");
            return false;
        } else if (binding.inputPass.getText().toString().trim().length() < 6) {
            ToastMessage("Şifreniz En Az 6 Karakter İçermeli");
            return false;
        }
        return true;
    }

    private void Intent(Activity activity) {
        Intent intent = new Intent(getApplicationContext(), activity.getClass());
        startActivity(intent);
    }

    private void saveUserValues(String email, String password) {
        database.collection(Constants.KEY_USER_COLLECTION)
                .whereEqualTo(Constants.KEY_USER_MAIL, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                preferenceManager.putString(Constants.KEY_USER_NAME, documentSnapshot.getString(Constants.KEY_USER_NAME));
                                preferenceManager.putString(Constants.KEY_USER_MAIL, documentSnapshot.getString(Constants.KEY_USER_MAIL));
                                preferenceManager.putString(Constants.KEY_USER_PASS, AESCrypt.encrypt(password));

                                preferenceManager.putBoolean(Constants.KEY_UPDATE_PASS, true);
                                Intent(new MainMenuActivity());
                                finish();
                            }
                        } catch (Exception e) {
                            ToastMessage(e.getMessage());
                        }
                    }
                });
    }
}