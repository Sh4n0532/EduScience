package com.example.eduscience.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.model.Quiz;
import com.example.eduscience.model.QuizResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class QuizResultActivity extends AppCompatActivity {

    private TextView txtQuizMark;
    private Button btnBack, btnLeaderboard;
    private String lessonId, userId;
    private int mark, totalMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // initialization
        txtQuizMark = findViewById(R.id.txtQuizMark);
        btnBack = findViewById(R.id.btnBack);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        totalMark = 0;

        // show quiz mark
        Intent intent = getIntent();
        txtQuizMark.setText(String.valueOf(QuizActivity.quizMark) + "/" + intent.getStringExtra("totalQuiz"));
        mark = QuizActivity.quizMark;
        lessonId = intent.getStringExtra("lessonId");

        saveQuizResult();
        clickBtnBack();
        clickBtnLeaderboard();
    }

    private void updateTotalMark() {
        FirebaseDatabase.getInstance().getReference("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userKey = dataSnapshot.getKey();
                    Query query = FirebaseDatabase.getInstance().getReference("quiz_result").orderByChild("userId").equalTo(userKey);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                totalMark = 0;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    QuizResult result = dataSnapshot.getValue(QuizResult.class);
                                    totalMark += result.getMark();
                                }
                                FirebaseDatabase.getInstance().getReference("user").child(userKey).child("totalMark").setValue(totalMark);
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

    private void clickBtnLeaderboard() {
        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizResultActivity.this, LeaderboardActivity.class));
            }
        });
    }

    private void saveQuizResult() {
        FirebaseDatabase.getInstance().getReference("quiz_result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    QuizResult result = dataSnapshot.getValue(QuizResult.class);
                    result.setId(dataSnapshot.getKey());

                    if(userId.equals(result.getUserId()) && lessonId.equals(result.getLessonId())) {
                        if(mark > result.getMark()) {
                            // update the highest mark
                            FirebaseDatabase.getInstance().getReference("quiz_result").child(result.getId()).child("mark").setValue(mark);
                        }
                        updateTotalMark();
                        return;
                    }
                }

                // first time quiz
                QuizResult newResult = new QuizResult(userId, lessonId, mark);
                String id = FirebaseDatabase.getInstance().getReference("quiz_result").push().getKey();
                FirebaseDatabase.getInstance().getReference("quiz_result").child(id).setValue(newResult);
                updateTotalMark();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickBtnBack() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizResultActivity.this, LessonActivity.class));
            }
        });
    }
}