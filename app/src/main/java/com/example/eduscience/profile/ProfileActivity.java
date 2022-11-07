package com.example.eduscience.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.eduscience.R;
import com.example.eduscience.authentication.LoginActivity;
import com.example.eduscience.discussion.DiscussionActivity;
import com.example.eduscience.discussion.EditPostActivity;
import com.example.eduscience.discussion.ViewPostActivity;
import com.example.eduscience.leaderboard.LeaderboardActivity;
import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView toolbarTitle, txtUsername;
    private EditText editTxtUsername;
    private TextInputLayout editTxtUsernameLayout;
    private BottomNavigationView navBar;
    private ImageView imgUser;
    private RelativeLayout btnMyPost, btnMyResult, btnChangePassword, btnContactUs;
    private Button btnLogout;
    private ProgressBar progressBar;
    private DatabaseReference dbRef;
    private final int GALLERY_REQUEST_CODE = 100;
    private Uri imageUri;
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        navBar = findViewById(R.id.navBar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        txtUsername = findViewById(R.id.txtUsername);
        editTxtUsername = findViewById(R.id.editTxtUsername);
        editTxtUsernameLayout = findViewById(R.id.editTxtUsernameLayout);
        imgUser = findViewById(R.id.imgUser);
        btnMyPost = findViewById(R.id.btnMyPost);
        btnMyResult = findViewById(R.id.btnMyResult);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnContactUs = findViewById(R.id.btnContactUs);
        btnLogout = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        sRef = FirebaseStorage.getInstance().getReference();

        navBar.setSelectedItemId(R.id.navProfile);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navLesson:
                        startActivity(new Intent(ProfileActivity.this, LessonActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navDiscussion:
                        startActivity(new Intent(ProfileActivity.this, DiscussionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navLeaderboard:
                        startActivity(new Intent(ProfileActivity.this, LeaderboardActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navProfile:
                        return true;
                }
                return false;
            }
        });

        toolbarTitle.setText("Profile");

        // functions
        switchView();
        getUserInfo();
        clickLogout();
        clickChangePassword();
        clickContactUs();
        clickMyPost();
        clickMyResult();
        clickImgUser();
    }

    private void switchView() {
        txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString().trim();
                txtUsername.setVisibility(View.GONE);
                editTxtUsername.setText(username);
                editTxtUsernameLayout.setVisibility(View.VISIBLE);
            }
        });

        editTxtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    String newUsername = editTxtUsername.getText().toString().trim();
                    if(newUsername.isEmpty()) {
                        editTxtUsername.setError("Required field!");
                        editTxtUsername.requestFocus();
                        return false;
                    }

                    editTxtUsername.clearFocus();
                    editTxtUsernameLayout.setVisibility(View.GONE);
                    txtUsername.setText(newUsername);
                    txtUsername.setVisibility(View.VISIBLE);

                    // save new username to database
                    FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Username updated successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ProfileActivity.this, "Update username failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return false;
            }
        });
    }

    private void clickImgUser() {
        imgUser.setOnClickListener(new View.OnClickListener() {
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
            imgUser.setImageURI(imageUri);
            imgUser.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // upload and change user profile image
            progressBar.setVisibility(View.VISIBLE);
            ContentResolver cr = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String fileExt = mime.getExtensionFromMimeType(cr.getType(imageUri));
            StorageReference fileRef = sRef.child("profile/" + System.currentTimeMillis() + "." + fileExt);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imgUrl").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ProfileActivity.this, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ProfileActivity.this, "Update profile picture failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileActivity.this, "Upload image failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clickMyResult() {
        btnMyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MyResultActivity.class));
            }
        });
    }

    private void clickMyPost() {
        btnMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MyPostActivity.class));
            }
        });
    }

    private void clickContactUs() {
        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ContactUsActivity.class));
            }
        });
    }

    private void clickChangePassword() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });
    }

    private void clickLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Logout"); // alert box title
                builder.setIcon(R.drawable.ic_warning);
                builder.setMessage("Are you sure you want to logout?");

                // confirm button
                builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    }
                });

                // cancel button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void getUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = dbRef.child("user").orderByKey().equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        txtUsername.setText(user.getUsername());

                        if(user.getImgUrl() != null) {
                            Glide.with(imgUser.getContext()).load(user.getImgUrl()).into(imgUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        navBar.setSelectedItemId(R.id.navProfile);
        super.onResume();
    }
}