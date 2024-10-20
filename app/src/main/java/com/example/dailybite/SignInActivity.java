package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin); // Make sure activity_login.xml exists and is referenced correctly

        // Initialize views by finding them by ID
        backButton = findViewById(R.id.back_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // Set OnClickListener for the back button to return to the previous activity
        backButton.setOnClickListener(v -> onBackPressed()); // This will handle navigating to the previous page/activity

        // Set OnClickListener for the login button
        loginButton.setOnClickListener(v -> {
            // Perform login logic or validation here
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Simple validation
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

            // Proceed with login logic
            Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignInActivity.this, Homepage.class);
            startActivity(intent);
        });

    }
}
