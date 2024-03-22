package com.example.startapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class actActivity extends AppCompatActivity {

    private Chip sedentaryChip;
    private Chip lightChip;
    private Chip modChip;
    private Chip highChip;
    private Chip extremeChip;
    private String selectedActivity = null;

    private Button nextButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        sedentaryChip= findViewById(R.id.sedentaryChip);
        lightChip = findViewById(R.id.lightChip);
        modChip = findViewById(R.id.modChip);
        highChip = findViewById(R.id.highChip);
        extremeChip = findViewById(R.id.extremeChip);

        nextButton = findViewById(R.id.nextBTN);

        // Set click listeners to Chips
        sedentaryChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(sedentaryChip);
                selectedActivity = "Sedentary";
            }
        });

        lightChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(lightChip);
                selectedActivity = "Lightly";
            }
        });

        modChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(modChip);
                selectedActivity = "Moderately";
            }
        });

        extremeChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(extremeChip);
                selectedActivity = "Extreme";
            }
        });

        highChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(highChip);
                selectedActivity = "High";
            }
        });



        // Set click listener to Next button
        nextButton.setOnClickListener(v -> {
            if (selectedActivity == null) {
                // Show error message if no goal is selected
                Toast.makeText(actActivity.this, "Please select a diet", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to update user's goal in Firestore
                updateUserGoal(selectedActivity);
            }
        });

        // Adjust padding based on system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void clearChipsExcept(Chip chip) {
        if (sedentaryChip != chip) sedentaryChip.setChecked(false);
        if (highChip != chip) highChip.setChecked(false);
        if (lightChip != chip) lightChip.setChecked(false);
        if (modChip != chip) modChip.setChecked(false);
        if (extremeChip != chip) extremeChip.setChecked(false);
    }

    private void updateUserGoal(String activity) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef
                    .update("activity", activity)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(actActivity.this, "Activity level updated successfully", Toast.LENGTH_SHORT).show();
                        // Proceed to the stats activity
                        startActivity(new Intent(actActivity.this, mealsActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(actActivity.this, "Failed to update activity level: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(actActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
