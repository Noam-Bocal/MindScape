package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // Method to handle login button click
    public void login(View v) {
        // Find email and password EditText fields
        EditText emailEditText = findViewById(R.id.usernamePT);
        EditText passwordEditText = findViewById(R.id.passwordPT);

        // Get text from EditText fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, start MainActivity
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, 0);
                    } else {
                        // Login failed, display error message
                        Exception exception = task.getException();
                        if (exception != null) {
                            Toast.makeText(LoginScreen.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to handle register button click
    public void register(View v) {
        // Start SignupScreen activity
        startActivity(new Intent(getApplicationContext(), SignupScreen.class));
        overridePendingTransition(android.R.anim.fade_out, 0);
    }
}
