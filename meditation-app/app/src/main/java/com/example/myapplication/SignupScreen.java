package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void Register(View v) {
        EditText emailEditText = findViewById(R.id.emailPT);
        EditText usernameEditText = findViewById(R.id.usernamePT);
        EditText passwordEditText = findViewById(R.id.passwordPT);
        EditText genderEditText = findViewById(R.id.genderPT);

        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid(); // You might want to use this as the document ID in Firestore

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("username", username);
                        userData.put("email", email);
                        userData.put("gender", gender);
                        userData.put("password", password);

                        db.collection("User").document(uid).set(userData) // Using UID as the document ID
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupScreen.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                                    overridePendingTransition(android.R.anim.fade_in, 0);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupScreen.this, "Failed to store user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(SignupScreen.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupScreen.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
