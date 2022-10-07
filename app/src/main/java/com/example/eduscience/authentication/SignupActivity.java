package com.example.eduscience.authentication;

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

import com.example.eduscience.R;
import com.example.eduscience.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText txtUsername, txtEmail, txtPassword1, txtPassword2;
    private Button btnReg, btnLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword1 = findViewById(R.id.txtPassword1);
        txtPassword2 = findViewById(R.id.txtPassword2);
        btnReg = findViewById(R.id.btnReg);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        clickRegister();
        clickLogin();
    }

    private void clickLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    private void clickRegister() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password1 = txtPassword1.getText().toString().trim();
                String password2 = txtPassword2.getText().toString().trim();

                // form validation
                if(username.isEmpty()) {
                    txtUsername.setError("Required field!");
                    txtUsername.requestFocus();
                    return;
                }

                if(email.isEmpty()) {
                    txtEmail.setError("Required field!");
                    txtEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    txtEmail.setError("Invalid email address!");
                    txtEmail.requestFocus();
                    return;
                }

                if(password1.isEmpty()) {
                    txtPassword1.setError("Required field!");
                    txtPassword1.requestFocus();
                    return;
                }

                if(password2.isEmpty()) {
                    txtPassword2.setError("Required field!");
                    txtPassword2.requestFocus();
                    return;
                }

                if (password1.length() < 8) {
                    txtPassword1.setError("Password should contains at least 8 characters!");
                    txtPassword1.requestFocus();
                    return;
                }

                if (!password1.equals(password2)) {
                    txtPassword2.setError("Password do not match!");
                    txtPassword2.requestFocus();
                    return;
                }

                // form is valid, register user
                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ // user is created but not stored into database yet
                            User user = new User(username, email, "User", 0);
                            FirebaseDatabase.getInstance().getReference("user")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
                                        curUser.sendEmailVerification();
                                        Toast.makeText(SignupActivity.this, "Account registered. A verification link has been sent to your email", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    }
                                    else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(SignupActivity.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignupActivity.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}