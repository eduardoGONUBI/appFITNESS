package com.example.startapp;

import android.content.Intent;
import android.os.Bundle;
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

public class focusActivity extends AppCompatActivity {

    private Chip shoulderChip;
    private Chip chestChip;
    private Chip armsChip;
    private Chip legsChip;
    private Chip backChip;
    private Chip glutesChip;
    private Chip balancedChip;
    private Button nextButton;
    private String selectedFocus = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_focus);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        shoulderChip = findViewById(R.id.shoulderChip);
        chestChip = findViewById(R.id.chestChip);
        armsChip = findViewById(R.id.armsChip);
        legsChip = findViewById(R.id.legsChip);
        backChip = findViewById(R.id.backChip);
        glutesChip = findViewById(R.id.glutesChip);
        balancedChip = findViewById(R.id.balancedChip);
        nextButton = findViewById(R.id.nextBTN);

        shoulderChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(shoulderChip);
                selectedFocus = "Shoulders";
            }
        });

        chestChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(chestChip);
                selectedFocus = "Chest";
            }
        });

        armsChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(armsChip);
                selectedFocus = "Arms";
            }
        });

        legsChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(legsChip);
                selectedFocus = "Legs";
            }
        });

        backChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(backChip);
                selectedFocus = "Back";
            }
        });

        glutesChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(glutesChip);
                selectedFocus = "Glutes";
            }
        });

        balancedChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(balancedChip);
                selectedFocus = "Balanced";
            }
        });

        // Set click listener to Next button
        nextButton.setOnClickListener(v -> {
            if (selectedFocus == null) {
                // Show error message if no goal is selected
                Toast.makeText(focusActivity.this, "Please select a focus", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to update user's goal in Firestore
                updateFocus(selectedFocus);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void clearChipsExcept(Chip chip){
        if (shoulderChip != chip) shoulderChip.setChecked(false);
        if (chestChip != chip) chestChip.setChecked(false);
        if (armsChip != chip) armsChip.setChecked(false);
        if (backChip != chip) backChip.setChecked(false);
        if (legsChip != chip) legsChip.setChecked(false);
        if (glutesChip != chip) glutesChip.setChecked(false);
        if (balancedChip != chip) balancedChip.setChecked(false);
    }

    private void updateFocus(String focus) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef
                    .update("focus", focus)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(focusActivity.this, "Focus updated successfully", Toast.LENGTH_SHORT).show();
                        // Proceed to the stats activity
                        startActivity(new Intent(focusActivity.this, testActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(focusActivity.this, "Failed to update goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(focusActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }



}