package com.example.eduscience.discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.Discussion;
import com.example.eduscience.model.User;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;

public class ViewPostActivity extends AppCompatActivity {

    private String postId;
    private BottomNavigationView navBar;
    private TextView toolbarTitle, txtUsername, txtCreatedOn, txtContent;
    private ImageView imgUser, imgPost;
    private DatabaseReference dbRef;
    private StorageReference sRef;
    private String userId;
    private Button btnEdit, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        txtUsername = findViewById(R.id.txtUsername);
        txtCreatedOn = findViewById(R.id.txtCreatedOn);
        txtContent = findViewById(R.id.txtContent);
        imgUser = findViewById(R.id.imgUser);
        imgPost = findViewById(R.id.imgPost);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        dbRef = FirebaseDatabase.getInstance().getReference();

        navBar.setSelectedItemId(R.id.navDiscussion);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(ViewPostActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(ViewPostActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(ViewPostActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(ViewPostActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Discussion");

        // function
        getPost();
        clickEdit();
        clickDelete();
    }

    private void clickDelete() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create alert dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                builder.setTitle("Delete"); // alert box title
                builder.setIcon(R.drawable.ic_warning);
                builder.setMessage("Are you sure you want to delete this post?");

                // positive button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete procedure
                        Query query = dbRef.child("discussion").orderByKey().equalTo(postId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        // delete image
                                        if(dataSnapshot.hasChild("imgUrl")) {
                                            String imgUrl = dataSnapshot.child("imgUrl").getValue(String.class);
                                            sRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
                                            sRef.delete();
                                        }

                                        // delete row
                                        dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(ViewPostActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ViewPostActivity.this, DiscussionActivity.class));
                                                }
                                                else {
                                                    Toast.makeText(ViewPostActivity.this, "Delete post failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                // negative button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // close the alert box
                    }
                });
                builder.show();
            }
        });
    }

    private void clickEdit() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPostActivity.this, EditPostActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });
    }

    private void getPost() {
        Query query = dbRef.child("discussion").orderByKey().equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Discussion discussion = dataSnapshot.getValue(Discussion.class);

                        userId = discussion.getUserId();
                        txtCreatedOn.setText(discussion.getCreatedOn());
                        txtContent.setText(discussion.getContent());
                        if(discussion.getImgUrl() != null) {
                            Glide.with(imgPost.getContext()).load(discussion.getImgUrl()).into(imgPost);
                        }
                        else {
                            imgPost.setVisibility(View.GONE);
                        }

                        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(currentUser.equals(userId)) {
                            btnEdit.setVisibility(View.VISIBLE);
                            btnDelete.setVisibility(View.VISIBLE);
                        }
                        else {
                            btnEdit.setVisibility(View.GONE);
                            btnDelete.setVisibility(View.GONE);
                        }
                    }

                    Query query1 = dbRef.child("user").orderByKey().equalTo(userId);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    txtUsername.setText(user.getUsername());
                                    if(user.getImgUrl() != null) {
                                        Glide.with(imgUser.getContext()).load(user.getImgUrl()).into(imgUser);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}