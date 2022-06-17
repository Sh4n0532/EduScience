package com.example.eduscience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // declaration
    private Button btnLogin, btnReg;
    private EditText txtUsername, txtEmail, txtPassword1, txtPassword2;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialization
        btnLogin = findViewById(R.id.btnLogin);
        btnReg = findViewById(R.id.btnReg);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword1 = findViewById(R.id.txtPassword1);
        txtPassword2 = findViewById(R.id.txtPassword2);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        // methods
        clickBtnLogin();
        clickBtnReg();
    }

    private void clickBtnReg() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get user input
                String username = txtUsername.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String pass1 = txtPassword1.getText().toString().trim();
                String pass2 = txtPassword2.getText().toString().trim();

                // form validation
                if (username.isEmpty()) {
                    txtUsername.setError("Required field!");
                    txtUsername.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    txtEmail.setError("Required field!");
                    txtEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmail.setError("Invalid email address!");
                    txtEmail.requestFocus();
                    return;
                }

                if (pass1.isEmpty()) {
                    txtPassword1.setError("Required field!");
                    txtPassword1.requestFocus();
                    return;
                }

                if (pass2.isEmpty()) {
                    txtPassword2.setError("Required field!");
                    txtPassword2.requestFocus();
                    return;
                }

                if (pass1.length() < 8) {
                    txtPassword1.setError("Password should contains at least 8 characters!");
                    txtPassword1.requestFocus();
                    return;
                }

                if (!pass1.equals(pass2)) {
                    txtPassword2.setError("Password do not match!");
                    txtPassword2.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE); // show the progress bar

                mAuth.createUserWithEmailAndPassword(email, pass1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(username, email); // the user object

                                    // save the user into the realtime database
                                    FirebaseDatabase.getInstance("https://eduscience-9560a-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification();

                                                Toast.makeText(RegisterActivity.this, "User registered. A verification link has been sent to the email.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);

                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });
    }

    private void clickBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}