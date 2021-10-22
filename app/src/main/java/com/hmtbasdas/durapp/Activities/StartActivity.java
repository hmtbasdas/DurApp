package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hmtbasdas.durapp.Models.AESCrypt;
import com.hmtbasdas.durapp.R;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.Utilities.NetworkChangeListener;
import com.hmtbasdas.durapp.Utilities.PreferenceManager;

public class StartActivity extends BaseActivity {

    private PreferenceManager preferenceManager;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isAccountHave()){
                    login();
                }
                else{
                    Intent(new FragmentActivity());
                    finish();
                }
            }
        }, 2400);
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        auth = FirebaseAuth.getInstance();
    }

    private void login(){
        try{
            auth.signInWithEmailAndPassword(preferenceManager.getString(Constants.KEY_USER_MAIL), AESCrypt.decrypt(preferenceManager.getString(Constants.KEY_USER_PASS)))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                preferenceManager.putBoolean(Constants.KEY_UPDATE_PASS, false);
                                Intent(new MainMenuActivity());
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent(new FragmentActivity());
                            finish();
                        }
                    });
        }
        catch (Exception e){
            preferenceManager.clear();
            Intent(new FragmentActivity());
            finish();
        }
    }

    private boolean isAccountHave(){
        return preferenceManager.getString(Constants.KEY_USER_MAIL) == null && preferenceManager.getString(Constants.KEY_USER_PASS) == null;
    }

    private void Intent(Activity activity){
        Intent intent = new Intent(getApplicationContext(), activity.getClass());
        startActivity(intent);
    }
}