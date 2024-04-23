package com.example.priyanshu_tripathi_p2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {

    FirebaseFirestore firestoreDB;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        final EditText editTextFullName = findViewById(R.id.editTextName);
        final EditText editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        final EditText editTextEmail = findViewById(R.id.editTextEmail);
        final EditText editTextAddress = findViewById(R.id.editTextAddress);
        final EditText editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        Button payNowButton = findViewById(R.id.btnPayNow);

        firestoreDB = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getEmail();

            if (!userId.isEmpty()) {
                userID = userId;
            }
        }

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CartActivity
                Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
                startActivity(intent);
                finish(); // Optional, depending on your navigation flow
            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editTextFullName.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String expiryDate = editTextExpiryDate.getText().toString().trim();

                // Validate full name
                if (fullName.isEmpty()) {
                    editTextFullName.setError("Full name is required");
                    return;
                }

                // Validate phone number
                if (!isValidPhoneNumber(phoneNumber)) {
                    editTextPhoneNumber.setError("Invalid phone number");
                    return;
                }

                // Validate email
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Invalid email address");
                    return;
                }

                // Validate address
                if (address.isEmpty()) {
                    editTextAddress.setError("Address is required");
                    return;
                } else if (!isValidAddress(address)) {
                    editTextAddress.setError("Invalid address format");
                    return;
                }

                // Validate expiry date
                if (!isValidExpiryDate(expiryDate)) {
                    editTextExpiryDate.setError("Expiry date must be in MMYYYY format");
                    return;
                }

                if (!isValidExpiryDate(expiryDate)) {
                    editTextExpiryDate.setError("Expiry date must be in MMYYYY format");
                    return;
                }
                    Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                    finish();
                    firestoreDB.collection("userCart")
                            .whereEqualTo("user_id", userID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            firestoreDB.collection("userCart").document(document.getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    } else {

                                    }
                                }
                            });


            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {

        return phoneNumber.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
               return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate address format
    private boolean isValidAddress(String address) {
        Pattern pattern = Pattern.compile("[^@!]+");
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    private boolean isValidExpiryDate(String expiryDate) {

        if (expiryDate.length() != 6) {
            return false;
        }

        String monthString = expiryDate.substring(0, 2);
        String yearString = expiryDate.substring(2);

        int month, year;
        try {
            month = Integer.parseInt(monthString);
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            return false;
        }

        return (month >= 3 && month <= 12) && (year >= 2024);
    }
}
