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

public class dietActivity extends AppCompatActivity {

    private Chip lowChip;
    private Chip balancedChip;
    private Chip highChip;
    private String selectedDiet = null;

    private Button nextButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        lowChip = findViewById(R.id.lowChip);
        balancedChip = findViewById(R.id.balancedChip);
        highChip = findViewById(R.id.highChip);
        nextButton = findViewById(R.id.nextBTN);

        // Set click listeners to Chips
        lowChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(lowChip);
                selectedDiet = "Low Carb";
            }
        });

        balancedChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(balancedChip);
                selectedDiet = "Balanced";
            }
        });

        highChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearChipsExcept(highChip);
                selectedDiet = "High Carb";
            }
        });

        // Set click listener to Next button
        nextButton.setOnClickListener(v -> {
            if (selectedDiet == null) {
                // Show error message if no goal is selected
                Toast.makeText(dietActivity.this, "Please select a diet", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to update user's goal in Firestore
                updateUserGoal(selectedDiet);
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
        if (balancedChip != chip) balancedChip.setChecked(false);
        if (highChip != chip) highChip.setChecked(false);
        if (lowChip != chip) lowChip.setChecked(false);
    }

    private void updateUserGoal(String diet) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef
                    .update("diet", diet)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(dietActivity.this, "Diet updated successfully", Toast.LENGTH_SHORT).show();
                        // Proceed to the stats activity
                        startActivity(new Intent(dietActivity.this, actActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(dietActivity.this, "Failed to update diet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(dietActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
