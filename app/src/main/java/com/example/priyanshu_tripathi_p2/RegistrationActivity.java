package com.example.priyanshu_tripathi_p2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextFirstname, editTextLastname;
    private Button buttonRegister;
    private ImageView backArrow;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFirstname = findViewById(R.id.firstname);
        editTextLastname = findViewById(R.id.lastname);
        buttonRegister = findViewById(R.id.registerBTN);
        backArrow = findViewById(R.id.backbtn);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String firstname = editTextFirstname.getText().toString().trim();
                String lastname = editTextLastname.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main activity
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it when pressing back
            }
        });
    }
}
