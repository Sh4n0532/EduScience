package com.example.eduscience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    // declaration
    private Button btnSignup, btnLogin;
    private TextView btnForgotPass;
    private EditText txtEmail, txtPassword;
    private ProgressBar progressBar;
    private CheckBox cbRememberMe;

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialization
        btnSignup = findViewById(R.id.btnSignup);
        btnForgotPass = findViewById(R.id.btnForgotPass);
        btnLogin = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        progressBar = findViewById(R.id.progressBar);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("loginPref", MODE_PRIVATE);
        editor = preferences.edit();

        // methods
        clickBtnSignup();
        clickBtnForgotPass();
        clickBtnLogin();

        remember = preferences.getBoolean("remember", false);
        if (remember == true) {
            txtEmail.setText(preferences.getString("email", ""));
            txtPassword.setText(preferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }
    }

    private void clickBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

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

                if (password.isEmpty()) {
                    txtPassword.setError("Required field!");
                    txtPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            progressBar.setVisibility(View.INVISIBLE);
                            if (user.isEmailVerified()) {
                                if (cbRememberMe.isChecked()) {
                                    editor.putBoolean("remember", true);
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.commit();
                                }
                                else {
                                    editor.clear();
                                    editor.commit();
                                }

                                Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, LessonActivity.class));
                            }
                            else {
                                user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void clickBtnForgotPass() {
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void clickBtnSignup() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

}