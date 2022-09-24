package com.example.eduscience.leaderboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LeaderboardActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        navBar.setSelectedItemId(R.id.navLeaderboard);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(LeaderboardActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(LeaderboardActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(LeaderboardActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Leaderboard");
    }
}