package com.example.eduscience.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduscience.R;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private BottomNavigationView navBar;
    private EditText txtPassword1, txtPassword2;
    private Button btnChangePassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        navBar = findViewById(R.id.navBar);
        txtPassword1 = findViewById(R.id.txtPassword1);
        txtPassword2 = findViewById(R.id.txtPassword2);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);

        navBar.setSelectedItemId(R.id.navProfile);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(ChangePasswordActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(ChangePasswordActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(ChangePasswordActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Change Password");

        // function
        clickChangePassword();
    }

    private void clickChangePassword() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password1 = txtPassword1.getText().toString().trim();
                String password2 = txtPassword2.getText().toString().trim();

                // form validation
                if(password1.isEmpty()) {
                    txtPassword1.setError("Required field!");
                    txtPassword1.requestFocus();
                    return;
                }

                if(password2.isEmpty()) {
                    txtPassword2.setError("Required field!");
                    txtPassword2.requestFocus();
                    return;
                }

                if (password1.length() < 8) {
                    txtPassword1.setError("Password should contains at least 8 characters!");
                    txtPassword1.requestFocus();
                    return;
                }

                if (!password1.equals(password2)) {
                    txtPassword2.setError("Password do not match!");
                    txtPassword2.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                FirebaseAuth.getInstance().getCurrentUser().updatePassword(password1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, "Update password failed. Please try again", Toast.LENGTH_SHORT).show();
                            txtPassword1.requestFocus();
                        }
                    }
                });
            }
        });
    }
}