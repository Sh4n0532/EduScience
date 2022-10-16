package com.example.eduscience.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.authentication.LoginActivity;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.discussion.ViewPostActivity;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtUsername;
    private BottomNavigationView navBar;
    private ImageView imgUser;
    private RelativeLayout btnMyPost, btnMyResult, btnChangePassword, btnContactUs;
    private Button btnLogout;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        txtUsername = findViewById(R.id.txtUsername);
        imgUser = findViewById(R.id.imgUser);
        btnMyPost = findViewById(R.id.btnMyPost);
        btnMyResult = findViewById(R.id.btnMyResult);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnContactUs = findViewById(R.id.btnContactUs);
        btnLogout = findViewById(R.id.btnLogout);
        dbRef = FirebaseDatabase.getInstance().getReference();

        navBar.setSelectedItemId(R.id.navProfile);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(ProfileActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(ProfileActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(ProfileActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navProfile:
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Profile");

        // functions
        getUsername();
        clickLogout();
        clickChangePassword();
        clickContactUs();
    }

    private void clickContactUs() {
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ContactUsActivity.class));
            }
        });
    }

    private void clickChangePassword() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });
    }

    private void clickLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Logout"); // alert box title
                builder.setIcon(R.drawable.ic_warning);
                builder.setMessage("Are you sure you want to logout?");

                // confirm button
                builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    }
                });

                // cancel button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void getUsername() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = dbRef.child("user").orderByKey().equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        txtUsername.setText(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}