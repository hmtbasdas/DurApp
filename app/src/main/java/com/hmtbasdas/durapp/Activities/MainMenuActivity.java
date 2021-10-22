package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hmtbasdas.durapp.Models.Post;
import com.hmtbasdas.durapp.R;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.Utilities.PreferenceManager;
import com.hmtbasdas.durapp.databinding.ActivityMainMenuBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MainMenuActivity extends BaseActivity {

    private ActivityMainMenuBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference databaseReference;
    private String instagramLink, youtubeLink, twitterLink;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private PreferenceManager preferenceManager;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        getToken();
        setListeners();
        getMyPostCount();
        getSocialLinks();
        setSocialLinks();
        if(preferenceManager.getBoolean(Constants.KEY_UPDATE_PASS)){
            updateUserPassword(preferenceManager.getString(Constants.KEY_USER_PASS));
        }
    }

    private void init(){
        auth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        firebaseFirestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("SocialLinks");
        databaseReference = database.getReference().child("Posts");
        firebaseUser = auth.getCurrentUser();

        builder = new AlertDialog.Builder(this);

        posts = new ArrayList<>();
    }

    private void setListeners(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent i = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        binding.imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clear();
                auth.signOut();

                Intent(new SignInActivity());
                finish();
            }
        });

        binding.sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new SharePostActivity());
            }
        });

        binding.imageNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new NoticeActivity());
            }
        });

        binding.posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser.isEmailVerified()){
                    Intent(new PostsActivity());
                }
                else{
                    createPopup();
                }
            }
        });

        binding.myPostBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new MessageBoxActivity());
            }
        });

        binding.emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent(new EmergencyActivity());
            }
        });
    }

    private void Intent(Activity activity){
        Intent intent = new Intent(getApplicationContext(), activity.getClass());
        startActivity(intent);
    }

    private void createDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Emin misiniz ?")
                .setMessage("Hesabınız Kalıcı Olarak Silinecek!")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(auth.getCurrentUser() != null){
                            auth.getCurrentUser().delete();

                            firebaseFirestore.collection(Constants.KEY_USER_COLLECTION)
                                    .whereEqualTo(Constants.KEY_USER_MAIL, preferenceManager.getString(Constants.KEY_USER_MAIL))
                                    .whereEqualTo(Constants.KEY_USER_PASS, preferenceManager.getString(Constants.KEY_USER_PASS))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                documentSnapshot.getReference().delete();
                                            }
                                        }
                                    });
                            preferenceManager.clear();
                            Toast.makeText(getApplicationContext(),"Hesabınız Silindi!",Toast.LENGTH_SHORT).show();
                            Intent(new SignInActivity());
                            finish();
                        }
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void getSocialLinks(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instagramLink = Objects.requireNonNull(snapshot.child("instagram").getValue()).toString();
                youtubeLink = Objects.requireNonNull(snapshot.child("youtube").getValue()).toString();
                twitterLink = Objects.requireNonNull(snapshot.child("twitter").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSocialLinks(){
        binding.instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentURL(instagramLink);
            }
        });

        binding.youtubeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentURL(youtubeLink);
            }
        });

        binding.twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentURL(twitterLink);
            }
        });
    }

    private void createPopup(){
        final View popupView = getLayoutInflater().inflate(R.layout.popup_mail, null);

        TextView popupText = popupView.findViewById(R.id.popupText);
        Button popupButton = popupView.findViewById(R.id.popupButton);
        ImageView popupClose = popupView.findViewById(R.id.closePopup);
        ProgressBar popupProgress = popupView.findViewById(R.id.progressBar);

        popupText.setText(R.string.popup_mail_verification_text);
        popupButton.setText(R.string.send);

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupButton.setVisibility(View.INVISIBLE);
                popupProgress.setVisibility(View.VISIBLE);
                sendEmailVerification();
            }
        });

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void IntentURL(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void sendEmailVerification(){
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Gönderildi", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_USER_COLLECTION).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                //.addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Unable to update token",Toast.LENGTH_SHORT).show());
    }

    private void getMyPostCount(){
        binding.myPostCount.setText("0");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post post = snapshot.getValue(Post.class);
                if(post != null){
                    if(!post.isRead() && Objects.equals(auth.getUid(), post.getUserID())  && post.isConfirmation() && !post.getReplyAdminContent().isEmpty()){
                        posts.add(post);
                    }
                }
                if(posts.size() > 0){
                    binding.myPostCount.setText(String.valueOf(posts.size()));
                }
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

    private void updateUserPassword(String password){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_USER_COLLECTION).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        documentReference.update(Constants.KEY_USER_PASS, password);
    }
}