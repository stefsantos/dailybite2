package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button loginButton;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        backButton = findViewById(R.id.back_button);

        // Handle back button press
        backButton.setOnClickListener(v -> finish());

        // Handle login button press (for account creation)
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(username, email, password);
            }
        });
    }

    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Show a success toast
                            Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            // Save additional user details in Firestore
                            saveUserDetails(user.getUid(), username, email);
                        }
                    } else {
                        // Display error message if account creation fails
                        Toast.makeText(CreateAccountActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String username, String email) {
        // Create a user object to store in Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        // Add user document to Firestore in the "users" collection with the UID as the document ID
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    // Show welcome message and navigate to Homepage on success
                    Toast.makeText(CreateAccountActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAccountActivity.this, Homepage.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Display an error if saving details fails
                    Toast.makeText(CreateAccountActivity.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
