package com.example.eduscience.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduscience.learning.LessonActivity;
import com.example.eduscience.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin, btnSignup;
    private TextView btnForgotPass;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnForgotPass = findViewById(R.id.btnForgotPass);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("loginPref", MODE_PRIVATE);
        editor = preferences.edit();

        clickLogin();
        clickSignup();
        clickForgotPass();

        remember = preferences.getBoolean("remember", false);
        if(remember == true){
            txtEmail.setText(preferences.getString("email", ""));
            txtPassword.setText(preferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }
    }

    private void clickForgotPass() {
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void clickSignup() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private void clickLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                // form validation
                if(email.isEmpty()){
                    txtEmail.setError("Required field!");
                    txtEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    txtEmail.setError("Invalid email address!");
                    txtEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    txtPassword.setError("Required field!");
                    txtPassword.requestFocus();
                    return;
                }

                // form is valid, login user
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()){
                                if(cbRememberMe.isChecked()){
                                    // setting value
                                    editor.putBoolean("remember", true);
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                }
                                else {
                                    editor.clear();
                                }
                                editor.commit(); // save changes

                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, LessonActivity.class));
                            }
                            else {
                                user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Please verify your email to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            txtEmail.requestFocus();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
        super.onBackPressed();
    }
}