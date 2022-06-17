package com.example.eduscience;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class LessonActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private Button btnUpload;
    private Uri uri;
    private final int GALLEY_REQUEST_CODE = 200;
    private DatabaseReference reference;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        imgPreview = findViewById(R.id.imgPreview);
        btnUpload = findViewById(R.id.btnUpload);
        reference = FirebaseDatabase.getInstance("https://eduscience-9560a-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Image");
        storage = FirebaseStorage.getInstance().getReference();

        clickImg();
        clickBtnUpload();
    }

    private void clickBtnUpload() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    // get image extension type
                    ContentResolver cr = getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String ext = mime.getExtensionFromMimeType(cr.getType(uri));

                    // save image into firebase storage
                    StorageReference storageRef = storage.child(System.currentTimeMillis() + "." + ext);
                    storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // save uploaded image url to firebase
                                    Image image = new Image(uri.toString());
                                    String imageId = reference.push().getKey(); // generate unique key value
                                    reference.child(imageId).setValue(image);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LessonActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(LessonActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clickImg() {
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLEY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLEY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            imgPreview.setImageURI(uri);
        }
    }
}