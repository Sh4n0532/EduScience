package com.example.eduscience.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.model.QuizResult;
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
    private DatabaseReference dbRef;
    private String lessonId;
    private int mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // initialization
        txtQuizMark = findViewById(R.id.txtQuizMark);
        btnBack = findViewById(R.id.btnBack);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        dbRef = FirebaseDatabase.getInstance().getReference();

        // show quiz mark
        Intent intent = getIntent();
        txtQuizMark.setText(String.valueOf(QuizActivity.quizMark) + "/" + intent.getStringExtra("totalQuiz"));
        mark = QuizActivity.quizMark;
        lessonId = intent.getStringExtra("lessonId");

        saveQuizResult();
        clickBtnBack();
        clickBtnLeaderboard();
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // current login user id
        Query query = dbRef.child("quiz_result").orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){ // done the quiz before
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        QuizResult result = dataSnapshot.getValue(QuizResult.class);
                        result.setId(dataSnapshot.getKey());
                        if(result.getLessonId().equals(lessonId)){
                            if(result.getMark() < mark){
                                // update the highest mark
                                HashMap newResult = new HashMap();
                                newResult.put("lessonId", lessonId);
                                newResult.put("userId", userId);
                                newResult.put("mark", mark);

                                FirebaseDatabase.getInstance().getReference("quiz_result")
                                        .child(result.getId())
                                        .updateChildren(newResult);
                            }
                        }
                    }
                }
                else { // 1st try of the quiz
                    QuizResult result = new QuizResult(userId, lessonId, mark);
                    String id = FirebaseDatabase.getInstance().getReference("quiz_result").push().getKey();
                    FirebaseDatabase.getInstance().getReference("quiz_result").child(id).setValue(result);
                }
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