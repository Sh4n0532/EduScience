package com.example.eduscience.adapter;

import android.content.Context;
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

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder> {

    Context context;
    ArrayList<Discussion> discussionList;

    public DiscussionAdapter(Context context, ArrayList<Discussion> discussionList) {
        this.context = context;
        this.discussionList = discussionList;
    }

    @NonNull
    @Override
    public DiscussionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discussion_item, parent, false);
        return new DiscussionAdapter.DiscussionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionViewHolder holder, int position) {
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

        holder.txtCreatedOn.setText(discussion.getCreatedOn());
        holder.txtContent.setText(discussion.getContent());
    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    public class DiscussionViewHolder extends RecyclerView.ViewHolder {

        LinearLayout discussionLayout;
        ImageView imgUser, imgPost;
        TextView txtUsername, txtCreatedOn, txtContent;

        public DiscussionViewHolder(@NonNull View itemView) {
            super(itemView);

            discussionLayout = itemView.findViewById(R.id.discussionLayout);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtCreatedOn = itemView.findViewById(R.id.txtCreatedOn);
            txtContent = itemView.findViewById(R.id.txtContent);


        }
    }
}
