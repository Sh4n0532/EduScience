package com.example.eduscience.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduscience.R;
import com.example.eduscience.model.Lesson;
import com.example.eduscience.model.QuizResult;
import com.example.eduscience.model.Tutorial;
import com.example.eduscience.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.MyResultViewHolder> {
    Context context;
    ArrayList<QuizResult> quizResultList;
    int tutorialCount = 0;
    Lesson lesson;

    public MyResultAdapter(Context context, ArrayList<QuizResult> quizResultList) {
        this.context = context;
        this.quizResultList = quizResultList;
    }

    @NonNull
    @Override
    public MyResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_result_item, parent, false);
        return new MyResultAdapter.MyResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyResultViewHolder holder, int position) {
        QuizResult result = quizResultList.get(position);

        String lessonId = result.getLessonId();

        // get lesson name
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child("lesson").orderByKey().equalTo(lessonId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        lesson = dataSnapshot.getValue(Lesson.class);
                        holder.txtLessonName.setText(lesson.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query1 = dbRef.child("tutorial").orderByChild("lessonId").equalTo(lessonId);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    tutorialCount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        tutorialCount += 1;
                    }
                    holder.txtMark.setText(String.valueOf(result.getMark()) + "/" + String.valueOf(tutorialCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return quizResultList.size();
    }

    public class MyResultViewHolder extends RecyclerView.ViewHolder {

        TextView txtLessonName, txtMark;

        public MyResultViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLessonName = itemView.findViewById(R.id.txtLessonName);
            txtMark = itemView.findViewById(R.id.txtMark);

        }
    }
}
