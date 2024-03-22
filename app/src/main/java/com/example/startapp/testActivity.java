package com.example.startapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class testActivity extends AppCompatActivity {

    private Drawable borderHighlightDrawable;
    private Drawable nonDrawable;
    private TextView recTex;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize drawables
        borderHighlightDrawable = getResources().getDrawable(R.drawable.border_highlight);
        nonDrawable = getResources().getDrawable(R.drawable.otherborder);

        // Find the ConstraintLayout containing the image, chooseBTN, and recTex
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayzzz);
        ConstraintLayout conlatwo = findViewById(R.id.conlatwo);
        ConstraintLayout conlathree = findViewById(R.id.conlathree);

        // Find the TextView inside constraintLayzzz
        recTex = findViewById(R.id.recTex);

        Button nextBtn = findViewById(R.id.nextBTN);

        // Set OnClickListener to the ConstraintLayout
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(testActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener to conlatwo
        conlatwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(testActivity.this, MainActivity.class);
                startActivity(intent);

                // Toggle backgrounds
                conlatwo.setBackground(borderHighlightDrawable);
                conlathree.setBackground(nonDrawable);
            }
        });

        // Set OnClickListener to conlathree
        conlathree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(testActivity.this, MainActivity.class);
                startActivity(intent);

                // Toggle backgrounds
                conlatwo.setBackground(nonDrawable);
                conlathree.setBackground(borderHighlightDrawable);
            }
        });

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Retrieve current user focus from Firestore and set the text of the TextView
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userFocus = document.getString("focus");
                                    // Set the text of the TextView
                                    recTex.setText(userFocus);
                                } else {
                                    Toast.makeText(testActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(testActivity.this, "Error fetching document: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        // Set OnClickListener to nextBTN
        nextBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           // Redirect to MainActivity
                                           Intent intent = new Intent(testActivity.this, dietActivity.class);
                                           startActivity(intent);

                                           // Store recommended program in Firestore
                                           FirebaseUser currentUser = mAuth.getCurrentUser();
                                           if (currentUser != null) {
                                               // Assuming you have the recommended program stored in a variable called recommendedProgram
                                               String program = recTex.getText().toString(); // Replace this with your recommended program

                                               // Update the user document in Firestore with the recommended program
                                               db.collection("users").document(currentUser.getUid())
                                                       .update("program", program)
                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void aVoid) {
                                                               Log.d(TAG, "Program updated successfully");
                                                           }
                                                       })
                                                       .addOnFailureListener(new OnFailureListener() {
                                                           @Override
                                                           public void onFailure(@NonNull Exception e) {
                                                               Log.w(TAG, "Error updating program", e);
                                                           }
                                                       });
                                           }
                                       }
                                   });

        // Set the initial backgrounds
        constraintLayout.setBackground(borderHighlightDrawable);
        conlatwo.setBackground(nonDrawable);
        conlathree.setBackground(nonDrawable);

        // Set padding based on system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}