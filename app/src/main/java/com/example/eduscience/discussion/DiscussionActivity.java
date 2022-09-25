package com.example.eduscience.discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DiscussionActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView toolbarTitle;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        btnAdd = findViewById(R.id.btnAdd);

        navBar.setSelectedItemId(R.id.navDiscussion);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(DiscussionActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(DiscussionActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(DiscussionActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Discussion");

        // function
        clickBtnAdd();
    }

    private void clickBtnAdd() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DiscussionActivity.this, AddPostActivity.class));
            }
        });
    }
}