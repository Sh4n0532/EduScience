package com.example.eduscience.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduscience.R;
import com.example.eduscience.adapter.TutorialAdapter;
import com.example.eduscience.model.Tutorial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtProgress;
    private RecyclerView recyclerView;
    private ImageView btnPrev, btnNext;

    private String lessonId;
    private int currentTutorialNo;
    private TutorialAdapter tutorialAdapter;
    private DatabaseReference dbRef;
    private ArrayList<Tutorial> tutorialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // intent data
        Intent intent = getIntent();
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(intent.getStringExtra("lessonName"));
        lessonId = intent.getStringExtra("lessonId");

        // initialization
        txtProgress = findViewById(R.id.txtProgress);
        recyclerView = findViewById(R.id.recyclerView);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tutorialList = new ArrayList<>();
        currentTutorialNo = 0;

        dbRef = FirebaseDatabase.getInstance().getReference();
        getTutorial();

        tutorialAdapter = new TutorialAdapter(this, tutorialList);
        recyclerView.setAdapter(tutorialAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        setSnapHelper();

        clickBtnPrev();
        clickBtnNext();
    }

    private void clickBtnNext() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTutorialNo < tutorialList.size() - 1) {
                    recyclerView.smoothScrollToPosition(currentTutorialNo + 1);
                }
            }
        });
    }

    private void clickBtnPrev() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTutorialNo > 0){
                    recyclerView.smoothScrollToPosition(currentTutorialNo - 1); // scroll back one page
                }
            }
        });
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView); // attach the recycler view to snap helper

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                currentTutorialNo = recyclerView.getLayoutManager().getPosition(view);
                txtProgress.setText(String.valueOf(currentTutorialNo + 1) + "/" + String.valueOf(tutorialList.size()));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getTutorial() {
        Query query = dbRef.child("tutorial").orderByChild("lessonId").equalTo(lessonId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Tutorial tutorial = dataSnapshot.getValue(Tutorial.class);
                        tutorial.setId(dataSnapshot.getKey());
                        tutorialList.add(tutorial);
                    }
                    tutorialAdapter.notifyDataSetChanged();
                    txtProgress.setText("1/" + String.valueOf(tutorialList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}