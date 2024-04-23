package com.example.priyanshu_tripathi_p2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login);
        Button registerButton = findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();
        checkUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegistrationActivity
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void checkUser(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, ProductListActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUser();
    }
}
