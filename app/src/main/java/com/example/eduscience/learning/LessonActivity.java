package com.example.eduscience.learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduscience.ARCamera;
import com.example.eduscience.R;
import com.example.eduscience.adapter.LessonAdapter;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.model.Lesson;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LessonActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private DatabaseReference dbRef;
    private LessonAdapter lessonAdapter;
    private ArrayList<Lesson> lessonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        navBar = findViewById(R.id.navBar);
        navBar.setSelectedItemId(R.id.navLesson);

        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(LessonActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(LessonActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(LessonActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Lessons");

        dbRef = FirebaseDatabase.getInstance().getReference("lesson");
        getLesson(); // get data from db

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lessonList = new ArrayList<>();
        lessonAdapter = new LessonAdapter(this, lessonList);
        recyclerView.setAdapter(lessonAdapter);
    }

    private void getLesson() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Lesson lesson = dataSnapshot.getValue(Lesson.class);
                    lesson.setId(dataSnapshot.getKey());
                    lessonList.add(lesson);
                }
                lessonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}