package com.example.startapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class genderActivity extends AppCompatActivity {

    private Chip maleChip;
    private Chip femaleChip;
    private Button nextButton;
    private String selectedGender = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gender);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        maleChip = findViewById(R.id.maleChip);
        femaleChip = findViewById(R.id.femaleChip);
        nextButton = findViewById(R.id.nextBTN);

        // Set click listeners to Chips
        maleChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                femaleChip.setChecked(false);
                selectedGender = "Male";
            }
        });

        femaleChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                maleChip.setChecked(false);
                selectedGender = "Female";
            }
        });

        // Set click listener to Next button
        nextButton.setOnClickListener(v -> {
            if (selectedGender == null) {
                // Show error message if no gender is selected
                Toast.makeText(genderActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to update user's gender in Firestore
                updateUserGender(selectedGender);
                startActivity(new Intent(genderActivity.this, goalActivity.class));
            }
        });

        // Adjust padding based on system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateUserGender(String gender) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef
                    .update("gender", gender)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(genderActivity.this, "Gender updated successfully", Toast.LENGTH_SHORT).show();
                        // Proceed to the next activity or perform any other action
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(genderActivity.this, "Failed to update gender: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(genderActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
