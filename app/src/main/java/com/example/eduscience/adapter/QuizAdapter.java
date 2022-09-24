package com.example.eduscience.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduscience.R;
import com.example.eduscience.learning.QuizActivity;
import com.example.eduscience.model.Quiz;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    Context context;
    ArrayList<Quiz> quizList;

    public QuizAdapter(Context context, ArrayList<Quiz> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizAdapter.QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.txtQuestion.setText(quiz.getQuestion());
        holder.btnOption1.setText(quiz.getOption1());
        holder.btnOption2.setText(quiz.getOption2());
        holder.btnOption3.setText(quiz.getOption3());
        holder.btnOption4.setText(quiz.getOption4());

        holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkAnswer(quiz.getAnswer());
            }
        });

    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {

        TextView txtQuestion;
        Button btnOption1, btnOption2, btnOption3, btnOption4, btnSubmit;

        String selectedAnswer;
        int selectedAnswerNo;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            btnOption1 = itemView.findViewById(R.id.btnOption1);
            btnOption2 = itemView.findViewById(R.id.btnOption2);
            btnOption3 = itemView.findViewById(R.id.btnOption3);
            btnOption4 = itemView.findViewById(R.id.btnOption4);
            btnSubmit = itemView.findViewById(R.id.btnSubmit);

            btnOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAnswer = String.valueOf(btnOption1.getText());
                    selectedAnswerNo = 1;
                    btnOption1.setBackgroundResource(R.drawable.selected_option_button);
                    btnOption2.setBackgroundResource(R.drawable.option_button);
                    btnOption3.setBackgroundResource(R.drawable.option_button);
                    btnOption4.setBackgroundResource(R.drawable.option_button);
                }
            });

            btnOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAnswer = String.valueOf(btnOption2.getText());
                    selectedAnswerNo = 2;
                    btnOption1.setBackgroundResource(R.drawable.option_button);
                    btnOption2.setBackgroundResource(R.drawable.selected_option_button);
                    btnOption3.setBackgroundResource(R.drawable.option_button);
                    btnOption4.setBackgroundResource(R.drawable.option_button);
                }
            });

            btnOption3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAnswer = String.valueOf(btnOption3.getText());
                    selectedAnswerNo = 3;
                    btnOption1.setBackgroundResource(R.drawable.option_button);
                    btnOption2.setBackgroundResource(R.drawable.option_button);
                    btnOption3.setBackgroundResource(R.drawable.selected_option_button);
                    btnOption4.setBackgroundResource(R.drawable.option_button);
                }
            });

            btnOption4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAnswer = String.valueOf(btnOption4.getText());
                    selectedAnswerNo = 4;
                    btnOption1.setBackgroundResource(R.drawable.option_button);
                    btnOption2.setBackgroundResource(R.drawable.option_button);
                    btnOption3.setBackgroundResource(R.drawable.option_button);
                    btnOption4.setBackgroundResource(R.drawable.selected_option_button);
                }
            });
        }

        private void checkAnswer(String answer){
            if(selectedAnswer.equals(answer))
            {
                switch (selectedAnswerNo) {
                    case 1:
                        btnOption1.setBackgroundResource(R.drawable.correct_answer);
                        break;
                    case 2:
                        btnOption2.setBackgroundResource(R.drawable.correct_answer);
                        break;
                    case 3:
                        btnOption3.setBackgroundResource(R.drawable.correct_answer);
                        break;
                    case 4:
                        btnOption4.setBackgroundResource(R.drawable.correct_answer);
                        break;
                }

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.correct_answer_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                    if(getAdapterPosition() < quizList.size() - 1){
                                        if(context instanceof QuizActivity){
                                            ((QuizActivity)context).scrollTo(getAdapterPosition() + 1);
                                            QuizActivity.quizMark += 1;
                                        }
                                    }
                                    else {
                                        if(context instanceof QuizActivity){
                                            ((QuizActivity)context).showQuizResultAct();
                                            QuizActivity.quizMark += 1;
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                });

                dialog.show();
            }
            else {
                switch (selectedAnswerNo) {
                    case 1:
                        btnOption1.setBackgroundResource(R.drawable.wrong_answer);
                        break;
                    case 2:
                        btnOption2.setBackgroundResource(R.drawable.wrong_answer);
                        break;
                    case 3:
                        btnOption3.setBackgroundResource(R.drawable.wrong_answer);
                        break;
                    case 4:
                        btnOption4.setBackgroundResource(R.drawable.wrong_answer);
                        break;
                }

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.wrong_answer_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);

                TextView txtFeedback = dialog.findViewById(R.id.txtFeedback);
                txtFeedback.setText("The correct answer is: " + answer);

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                    if(getAdapterPosition() < quizList.size() - 1){
                                        if(context instanceof QuizActivity){
                                            ((QuizActivity)context).scrollTo(getAdapterPosition() + 1);
                                        }
                                    }
                                    else {
                                        if(context instanceof QuizActivity){
                                            ((QuizActivity)context).showQuizResultAct();
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                });

                dialog.show();
            }
        }
    }
}
