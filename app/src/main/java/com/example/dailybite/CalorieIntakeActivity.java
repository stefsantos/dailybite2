package com.example.dailybite;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalorieIntakeActivity extends AppCompatActivity {

    private TextView caloriesText, proteinsText, fatsText, carbsText, waterText;
    private Button saveButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_intake);


        caloriesText = findViewById(R.id.caloriesText);
        proteinsText = findViewById(R.id.proteinsText);
        fatsText = findViewById(R.id.fatsText);
        carbsText = findViewById(R.id.carbsText);
        waterText = findViewById(R.id.waterText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button_calorie_intake);

        backButton.setOnClickListener(v -> finish());

        // Set default values (hardcoded for now later can be fetched from a database or API)
        caloriesText.setText("3400 cal");
        proteinsText.setText("225 g");
        fatsText.setText("118 g");
        carbsText.setText("340 g");
        waterText.setText("2500 ml");


        caloriesText.setOnClickListener(v -> showEditDialog("Edit Calories", caloriesText, "cal"));
        proteinsText.setOnClickListener(v -> showEditDialog("Edit Proteins", proteinsText, "g"));
        fatsText.setOnClickListener(v -> showEditDialog("Edit Fats", fatsText, "g"));
        carbsText.setOnClickListener(v -> showEditDialog("Edit Carbs", carbsText, "g"));
        waterText.setOnClickListener(v -> showEditDialog("Edit Water", waterText, "ml"));

        saveButton.setOnClickListener(v -> {

            Toast.makeText(CalorieIntakeActivity.this, "Information saved!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(String title, TextView textViewToUpdate, String unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalorieIntakeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_value, null);
        builder.setView(dialogView);

        EditText valueInput = dialogView.findViewById(R.id.editValueInput);
        valueInput.setText(textViewToUpdate.getText().toString().replace(unit, "").trim());

        builder.setTitle(title)
                .setPositiveButton("Done", (dialog, id) -> {
                    String newValue = valueInput.getText().toString() + " " + unit;
                    textViewToUpdate.setText(newValue);
                    Toast.makeText(CalorieIntakeActivity.this, title + " updated to: " + newValue, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
