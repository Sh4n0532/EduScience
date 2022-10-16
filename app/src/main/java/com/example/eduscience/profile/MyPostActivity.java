package com.example.eduscience.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.adapter.DiscussionAdapter;
import com.example.eduscience.adapter.MyPostAdapter;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.Discussion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyPostActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private BottomNavigationView navBar;
    private RecyclerView recyclerView;
    private DatabaseReference dbRef;
    private ArrayList<Discussion> discussionList;
    private MyPostAdapter myPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        navBar = findViewById(R.id.navBar);
        recyclerView = findViewById(R.id.recyclerView);
        dbRef = FirebaseDatabase.getInstance().getReference();
        discussionList = new ArrayList<>();

        navBar.setSelectedItemId(R.id.navProfile);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(MyPostActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(MyPostActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(MyPostActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(MyPostActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("My Posts");

        getDiscussion();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myPostAdapter = new MyPostAdapter(this, discussionList);
        recyclerView.setAdapter(myPostAdapter);
    }

    private void getDiscussion() {
        Query query = dbRef.child("discussion").orderByChild("createdOn");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Discussion discussion = dataSnapshot.getValue(Discussion.class);
                        discussion.setId(dataSnapshot.getKey());
                        if(discussion.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            discussionList.add(discussion);
                        }
                    }
                    Collections.reverse(discussionList);
                    myPostAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}