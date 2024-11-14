package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        // Check if user is logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // User is logged in, redirect to HomeActivity
            Intent intent = new Intent(MainActivity.this, Homepage.class);
            startActivity(intent);
            finish(); // Close MainActivity so it doesn't stay in the back stack
            return;
        }

        setContentView(R.layout.activity_main);

        // Adjust padding based on system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the Start button and set OnClickListener
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set login state in SharedPreferences
                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();

                // Navigate to WeightgoalActivity
                Intent intent = new Intent(MainActivity.this, WeightgoalActivity.class);
                startActivity(intent);
            }
        });

        // Find the Sign-in text and set OnClickListener
        TextView signInText = findViewById(R.id.sign_in_text);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set login state in SharedPreferences
                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();

                // Navigate to SignInActivity
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
