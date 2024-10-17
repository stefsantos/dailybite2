package com.example.dailybite;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the plus icon for adding meals within the fragment's view
        ImageView plusIconMeal = view.findViewById(R.id.plus_icon_meal);
        plusIconMeal.setOnClickListener(v -> openCalendar());

        return view;
    }

    // Moved the calendar logic from Homepage activity to HomeFragment
    private void openCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    navigateToMealInput(selectedYear, selectedMonth + 1, selectedDay);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Moved the navigation to meal input from Homepage activity to HomeFragment
    private void navigateToMealInput(int year, int month, int day) {
        Intent intent = new Intent(getActivity(), meal_input.class);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        startActivity(intent);
    }
}