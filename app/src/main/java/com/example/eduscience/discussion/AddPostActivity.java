package com.example.eduscience.discussion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduscience.R;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.Discussion;
import com.example.eduscience.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private TextView toolbarTitle;
    private ImageView btnChooseImage;
    private EditText txtContent;
    private Button btnPost;
    private ProgressBar progressBar;
    private Uri imageUri;
    private final int GALLERY_REQUEST_CODE = 100;
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        txtContent = findViewById(R.id.txtContent);
        btnPost = findViewById(R.id.btnPost);
        progressBar = findViewById(R.id.progressBar);
        sRef = FirebaseStorage.getInstance().getReference();

        navBar.setSelectedItemId(R.id.navLesson);

        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(AddPostActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(AddPostActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(AddPostActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navProfile:
                        startActivity(new Intent(AddPostActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Discussion");

        // function
        clickBtnChooseImg();
        clickBtnPost();
    }

    private void clickBtnPost() {
        btnPost.setOnClickListener(new View.OnClickListener() {
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

                // save to database
                Discussion discussion = new Discussion();

                // content
                discussion.setContent(content);

                // user id
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                discussion.setUserId(userId);

                // created date
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String createdOn = dateFormat.format(date).toString();
                discussion.setCreatedOn(createdOn);

                discussion.setApprove(false);

                // primary key
                String id = FirebaseDatabase.getInstance().getReference("discussion").push().getKey();

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
                                    FirebaseDatabase.getInstance().getReference("discussion").child(id).setValue(discussion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(AddPostActivity.this, "Post created successfully.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddPostActivity.this, DiscussionActivity.class));
                                            }
                                            else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(AddPostActivity.this, "Create post failed. Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPostActivity.this, "Upload image failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else { // no image
                    FirebaseDatabase.getInstance().getReference("discussion").child(id).setValue(discussion).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddPostActivity.this, "Post created successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddPostActivity.this, DiscussionActivity.class));
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddPostActivity.this, "Create post failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void clickBtnChooseImg() {
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
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
            btnChooseImage.setImageURI(imageUri);
            btnChooseImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}