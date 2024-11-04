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

public class SignInActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        backButton = findViewById(R.id.back_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // Set OnClickListener for the back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Set OnClickListener for the login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate inputs
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                emailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Password is required");
                passwordInput.requestFocus();
                return;
            }

            // Sign in with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in success, navigate to the Homepage
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, Homepage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign-in failure, display a message to the user
                            Toast.makeText(SignInActivity.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
