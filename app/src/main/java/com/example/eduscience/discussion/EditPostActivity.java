package com.example.eduscience.discussion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.Discussion;
import com.example.eduscience.model.User;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditPostActivity extends AppCompatActivity {

    private String postId;
    private BottomNavigationView navBar;
    private TextView toolbarTitle;
    private ImageView imgPost;
    private EditText txtContent;
    private Button btnSave;
    private DatabaseReference dbRef;
    private final int GALLERY_REQUEST_CODE = 100;
    private Uri imageUri;
    private ProgressBar progressBar;
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imgPost = findViewById(R.id.imgPost);
        txtContent = findViewById(R.id.txtContent);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        sRef = FirebaseStorage.getInstance().getReference();

        navBar.setSelectedItemId(R.id.navDiscussion);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(EditPostActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(EditPostActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(EditPostActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(EditPostActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Discussion");

        // function
        getPost();
        clickImgPost();
        clickBtnSave();
    }

    private void clickBtnSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = txtContent.getText().toString().trim();

                // validation
                if(content.isEmpty()) {
                    txtContent.setError("Required field");
                    txtContent.requestFocus();
                    return;
                }

                // form is valid, create the post
                progressBar.setVisibility(View.VISIBLE);

                Discussion discussion = new Discussion();
                discussion.setContent(content);
                discussion.setId(postId);

                // upload image to firebase storage if image is chosen
                if(imageUri != null) {
                    ContentResolver cr = getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String fileExt = mime.getExtensionFromMimeType(cr.getType(imageUri));
                    StorageReference fileRef = sRef.child("discussion/" + System.currentTimeMillis() + "." + fileExt);
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    discussion.setImgUrl(uri.toString());
                                    FirebaseDatabase.getInstance().getReference("discussion").child(discussion.getId()).child("imgUrl").setValue(discussion.getImgUrl());
                                    FirebaseDatabase.getInstance().getReference("discussion").child(discussion.getId()).child("content").setValue(discussion.getContent()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(EditPostActivity.this, "Post updated successfully.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(EditPostActivity.this, DiscussionActivity.class));
                                            }
                                            else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(EditPostActivity.this, "Update post failed. Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditPostActivity.this, "Upload image failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else { // no image
                    FirebaseDatabase.getInstance().getReference("discussion").child(discussion.getId()).child("content").setValue(discussion.getContent()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditPostActivity.this, "Post updated successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditPostActivity.this, DiscussionActivity.class));
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditPostActivity.this, "Update post failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void clickImgPost() {
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            imgPost.setImageURI(imageUri);
            imgPost.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void getPost() {
        Query query = dbRef.child("discussion").orderByKey().equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Discussion discussion = dataSnapshot.getValue(Discussion.class);

                        txtContent.setText(discussion.getContent());
                        if(discussion.getImgUrl() != null) {
                            Glide.with(imgPost.getContext()).load(discussion.getImgUrl()).into(imgPost);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}