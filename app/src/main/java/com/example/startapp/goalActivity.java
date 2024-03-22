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

public class goalActivity extends AppCompatActivity {

    private Chip muscleChip;
    private Chip loseFatChip;
    private Chip recompChip;
    private Chip strongAFChip;
    private Button nextButton;
    private String selectedGoal = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        muscleChip = findViewById(R.id.muscleChip);
        loseFatChip = findViewById(R.id.loseFatChip);
        recompChip = findViewById(R.id.recompChip);
        strongAFChip = findViewById(R.id.strongAFChip);
        nextButton = findViewById(R.id.nextBTN);

        // Set click listeners to Chips
        muscleChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(muscleChip);
                selectedGoal = "Build Muscle";
            }
        });

        loseFatChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(loseFatChip);
                selectedGoal = "Lose Body Fat";
            }
        });

        recompChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(recompChip);
                selectedGoal = "Recomp";
            }
        });

        strongAFChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(strongAFChip);
                selectedGoal = "Get Strong AF";
            }
        });

        // Set click listener to Next button
        nextButton.setOnClickListener(v -> {
            if (selectedGoal == null) {
                // Show error message if no goal is selected
                Toast.makeText(goalActivity.this, "Please select a goal", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to update user's goal in Firestore
                updateUserGoal(selectedGoal);
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
        if (muscleChip != chip) muscleChip.setChecked(false);
        if (loseFatChip != chip) loseFatChip.setChecked(false);
        if (recompChip != chip) recompChip.setChecked(false);
        if (strongAFChip != chip) strongAFChip.setChecked(false);
    }

    private void updateUserGoal(String goal) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef
                    .update("goal", goal)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(goalActivity.this, "Goal updated successfully", Toast.LENGTH_SHORT).show();
                        // Proceed to the stats activity
                        startActivity(new Intent(goalActivity.this, statsActivity.class));
                        })
                    .addOnFailureListener(e -> {
                        Toast.makeText(goalActivity.this, "Failed to update goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(goalActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
