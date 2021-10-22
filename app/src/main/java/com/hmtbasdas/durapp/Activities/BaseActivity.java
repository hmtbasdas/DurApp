package com.hmtbasdas.durapp.Activities;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hmtbasdas.durapp.R;
import com.hmtbasdas.durapp.Utilities.NetworkChangeListener;
import com.hmtbasdas.durapp.Utilities.PreferenceManager;


public class BaseActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private PreferenceManager preferenceManager;

    private boolean systemStatus;

    private NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("System");

        preferenceManager = new PreferenceManager(getApplicationContext());

        networkChangeListener = new NetworkChangeListener();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onResume();
        getSystemStatus();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void getSystemStatus(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                systemStatus = (Boolean) snapshot.child("status").getValue();
                if(!systemStatus){
                    preferenceManager.clear();
                    createAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void createAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FullScreen);
        View layout_dialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.system_off, null);
        builder.setView(layout_dialog);

        Button exit = layout_dialog.findViewById(R.id.exit);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

}