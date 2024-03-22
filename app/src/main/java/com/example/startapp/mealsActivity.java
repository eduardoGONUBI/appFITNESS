package com.example.startapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class mealsActivity extends AppCompatActivity {

    private Spinner numberSpinner;
    private Button nextBTN;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup the Spinner
        numberSpinner = findViewById(R.id.numberSpinner);
        Integer[] numbers = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        numberSpinner.setAdapter(adapter);

        // Setup the Button
        nextBTN = findViewById(R.id.nextBTN);
        nextBTN.setOnClickListener(v -> saveUserMealChoice());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void saveUserMealChoice() {
        // Get the currently logged-in user
        String userId = mAuth.getCurrentUser().getUid();
        // Get the selected meal number
        int mealsPerDay = (int) numberSpinner.getSelectedItem();

        // Save the selection in Firestore
        db.collection("users").document(userId)
                .update("meals", mealsPerDay)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(mealsActivity.this, "Number of meals updated successfully", Toast.LENGTH_SHORT).show();
                    // Proceed to the stats activity
                    startActivity(new Intent(mealsActivity.this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    // Handle Failure
                });
    }
}
