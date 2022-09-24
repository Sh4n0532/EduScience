package com.example.eduscience.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduscience.R;
import com.example.eduscience.learning.QuizActivity;
import com.example.eduscience.learning.TutorialActivity;
import com.example.eduscience.model.Lesson;

import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    Context context;
    ArrayList<Lesson> lessonList;

    public LessonAdapter(Context context, ArrayList<Lesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.lessonName.setText(lesson.getName());
        Glide.with(holder.lessonImg.getContext()).load(lesson.getImgUrl()).into(holder.lessonImg);
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {

        ImageView lessonImg;
        TextView lessonName, btnTutorial, btnQuiz;
        LinearLayout lessonLayout;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            lessonImg = itemView.findViewById(R.id.lessonImg);
            lessonName = itemView.findViewById(R.id.lessonName);
            btnTutorial = itemView.findViewById(R.id.btnTutorial);
            btnQuiz = itemView.findViewById(R.id.btnQuiz);
            lessonLayout = itemView.findViewById(R.id.lessonLayout);

            lessonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btnTutorial.isShown() && btnQuiz.isShown()){
                        btnTutorial.setVisibility(View.GONE);
                        btnQuiz.setVisibility(View.GONE);
                    }
                    else {
                        btnTutorial.setVisibility(View.VISIBLE);
                        btnQuiz.setVisibility(View.VISIBLE);
                    }
                }
            });

            btnTutorial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Lesson lesson = lessonList.get(position);
                    Intent intent = new Intent(context, TutorialActivity.class);
                    intent.putExtra("lessonId", lesson.getId());
                    intent.putExtra("lessonName", lesson.getName());
                    context.startActivity(intent);
                }
            });

            btnQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Lesson lesson = lessonList.get(position);
                    Intent intent = new Intent(context, QuizActivity.class);
                    intent.putExtra("lessonId", lesson.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
