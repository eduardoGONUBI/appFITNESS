package com.example.startapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class statsActivity extends AppCompatActivity {

    EditText age, height, weight;
    Button next;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        next = findViewById(R.id.nextBTN);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ageT = age.getText().toString().trim();
                String heightT = height.getText().toString().trim();
                String weightT = weight.getText().toString().trim();

                FirebaseUser user = mAuth.getCurrentUser();

                if(TextUtils.isEmpty(ageT)){
                    age.setError("Age is required!");
                    return;
                }

                if(TextUtils.isEmpty(heightT)){
                    height.setError("Height is required!");
                    return;
                }

                if(TextUtils.isEmpty(weightT)){
                    weight.setError("Weight is required!");
                    return;
                }

                if(user!=null){
                    String userID = user.getUid();
                    DocumentReference userRef = db.collection("users").document(userID);
                    userRef.update("height", heightT);
                    userRef.update("age", ageT);
                    userRef.update("weight", weightT);
                    startActivity(new Intent(statsActivity.this, focusActivity.class));
                    Toast.makeText(statsActivity.this, "Stats Updated with Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(statsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}