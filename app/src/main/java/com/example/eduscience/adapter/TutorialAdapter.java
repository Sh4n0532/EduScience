package com.example.eduscience.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduscience.ARCamera;
import com.example.eduscience.R;
import com.example.eduscience.model.Tutorial;

import java.util.ArrayList;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {
    Context context;
    ArrayList<Tutorial> tutorialList;

    public TutorialAdapter(Context context, ArrayList<Tutorial> tutorialList) {
        this.context = context;
        this.tutorialList = tutorialList;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tutorial_item, parent, false);
        return new TutorialAdapter.TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        Tutorial tutorial = tutorialList.get(position);
        Glide.with(holder.tutImg.getContext()).load(tutorial.getImgUrl()).into(holder.tutImg);
        holder.txtTutContent.setText(tutorial.getContent());

        if(tutorial.getModelUrl() == null) {
            holder.btnViewModel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }

    public class TutorialViewHolder extends RecyclerView.ViewHolder {

        ImageView tutImg;
        TextView txtTutContent;
        Button btnViewModel;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);

            tutImg = itemView.findViewById(R.id.tutImg);
            txtTutContent = itemView.findViewById(R.id.txtTutContent);
            btnViewModel = itemView.findViewById(R.id.btnViewModel);

            btnViewModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Tutorial tutorial = tutorialList.get(position);
                    Intent intent = new Intent(context, ARCamera.class);
                    intent.putExtra("modelUrl", tutorial.getModelUrl());
                    context.startActivity(intent);
                }
            });
        }
    }
}
