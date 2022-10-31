package com.example.eduscience.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduscience.R;
import com.example.eduscience.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    Context context;
    ArrayList<User> userList;

    public LeaderboardAdapter(Context context, ArrayList<User> userList)
    {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.LeaderboardViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtRanking.setText(String.valueOf(position + 1));
        holder.txtTotalMark.setText(String.valueOf(user.getTotalMark()));
        holder.txtUsername.setText(user.getUsername());

        if(user.getImgUrl() != null) {
            Glide.with(holder.imgUser.getContext()).load(user.getImgUrl()).into(holder.imgUser);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView txtRanking, txtUsername, txtTotalMark;
        ImageView imgUser;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRanking = itemView.findViewById(R.id.txtRanking);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtTotalMark = itemView.findViewById(R.id.txtTotalMark);
            imgUser = itemView.findViewById(R.id.imgUser);
        }
    }
}
