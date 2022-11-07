package com.example.eduscience.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.adapter.QuizAdapter;
import com.example.eduscience.model.Quiz;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView txtProgress, txtTimer;
    private RecyclerView recyclerView;

    private String lessonId;
    private QuizAdapter quizAdapter;
    private DatabaseReference dbRef;
    private ArrayList<Quiz> quizList;

    public static int quizMark;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // initialization
        Intent intent = getIntent();
        lessonId = intent.getStringExtra("lessonId");
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        recyclerView = findViewById(R.id.recyclerView);
        txtTimer = findViewById(R.id.txtTimer);
        quizMark = 0;

        quizList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference();
        getQuiz();

        quizAdapter = new QuizAdapter(this, quizList);
        recyclerView.setAdapter(quizAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        startTimer(); // timer for the quiz session
    }

    public void scrollTo(int position) {
        recyclerView.smoothScrollToPosition(position);
        progressBar.setProgress(position + 1);
        txtProgress.setText((position + 1) + "/" + quizList.size());
    }

    public void showQuizResultAct() {
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("totalQuiz", String.valueOf(quizList.size()));
        intent.putExtra("lessonId", lessonId);
        startActivity(intent);
    }

    private void startTimer() {
        long time = 900000; // 15 minutes

        timer = new CountDownTimer(time + 1000, 1000) {
            @Override
            public void onTick(long timeRemain) { // this function is called every second
                String timeRemainFormat = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(timeRemain),
                        TimeUnit.MILLISECONDS.toSeconds(timeRemain) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemain)));

                txtTimer.setText(timeRemainFormat);
            }

            @Override
            public void onFinish() {
                showQuizResultAct();
            }
        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer != null) {
            timer.cancel();
        }
    }

    private void getQuiz() {
        Query query = dbRef.child("quiz").orderByChild("lessonId").equalTo(lessonId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Quiz quiz = dataSnapshot.getValue(Quiz.class);
                        quiz.setId(dataSnapshot.getKey());
                        quizList.add(quiz);
                    }
                    quizAdapter.notifyDataSetChanged();
                    txtProgress.setText("1/" + String.valueOf(quizList.size()));
                    progressBar.setMax(quizList.size());
                    progressBar.setProgress(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}