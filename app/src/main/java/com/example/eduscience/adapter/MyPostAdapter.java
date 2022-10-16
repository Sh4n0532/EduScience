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
import com.example.eduscience.discussion.ViewPostActivity;
import com.example.eduscience.model.Discussion;
import com.example.eduscience.model.Lesson;
import com.example.eduscience.model.Tutorial;
import com.example.eduscience.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder> {

    Context context;
    ArrayList<Discussion> discussionList;

    public MyPostAdapter(Context context, ArrayList<Discussion> discussionList) {
        this.context = context;
        this.discussionList = discussionList;
    }

    @NonNull
    @Override
    public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_post_item, parent, false);
        return new MyPostAdapter.MyPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostViewHolder holder, int position) {
        Discussion discussion = discussionList.get(position);

        // get username
        String userId = discussion.getUserId();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child("user").orderByKey().equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        holder.txtUsername.setText(user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(discussion.getImgUrl() != null) {
            Glide.with(holder.imgPost.getContext()).load(discussion.getImgUrl()).into(holder.imgPost);
        }
        else {
            holder.imgPost.setVisibility(View.GONE);
        }

        if (discussion.getApprove()) {
            holder.txtStatus.setText("Approved");
        }
        else {
            holder.txtStatus.setText("Pending");
        }

        holder.txtCreatedOn.setText(discussion.getCreatedOn());
        holder.txtContent.setText(discussion.getContent());
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder {

        LinearLayout discussionLayout;
        ImageView imgUser, imgPost;
        TextView txtUsername, txtCreatedOn, txtContent, txtStatus;

        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);

            discussionLayout = itemView.findViewById(R.id.discussionLayout);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtCreatedOn = itemView.findViewById(R.id.txtCreatedOn);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            discussionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Discussion discussion = discussionList.get(position);
                    Intent intent = new Intent(context, ViewPostActivity.class);
                    intent.putExtra("postId", discussion.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
