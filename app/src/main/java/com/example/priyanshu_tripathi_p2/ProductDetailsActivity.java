package com.example.priyanshu_tripathi_p2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.priyanshu_tripathi_p2.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productDetailImage;
    private TextView productDetailName,productQuantity;
    private TextView productDetailPrice;
    private TextView productDetailDescription;
    int Id;
    String Name;
    int Quantity;
    double Price;
    String Image_link;
    String desc;
    Button addToCart;
    String userID;
    FirebaseFirestore firestoreDB;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Product product;
    ImageView backButton,cartButton;

    Button buttonIncrease,buttonDecrease;
    int qua = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        cartButton = findViewById(R.id.cartButton);
        productQuantity = findViewById(R.id.productQuantity);
        addToCart = findViewById(R.id.addToCart);
        productDetailImage = findViewById(R.id.productDetailImage);
        productDetailName = findViewById(R.id.productDetailName);
        productDetailPrice = findViewById(R.id.productDetailPrice);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        backButton = findViewById(R.id.backButton);
        buttonIncrease = findViewById(R.id.buttonIncrease);
        buttonDecrease = findViewById(R.id.buttonDecrease);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestoreDB = FirebaseFirestore.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getEmail();
            if (!userId.isEmpty()) {
                userID = userId;
            }
        }
        product = new Product();

        Intent intent = getIntent();
        Id = intent.getIntExtra("Id", 1);
        Name = intent.getStringExtra("Name");
        Quantity = intent.getIntExtra("Quantity", 1);
        Price = intent.getDoubleExtra("Price", 0.0);
        Image_link = intent.getStringExtra("Image_link");
        desc = intent.getStringExtra("Specifications");
        if (intent != null) {
            Glide.with(this)
                    .load(Image_link)
                    .placeholder(R.drawable.logo)
                    .into(productDetailImage);

            productDetailName.setText(Name);
//            productDetailPrice.setText(Double.toString(Price));
            productDetailPrice.setText("$"+Double.toString(Price));
            productDetailDescription.setText(desc);
        }

        buttonIncrease.setOnClickListener(v -> {
            qua++;
            product.setQuantity(qua);
            productQuantity.setText(String.valueOf(qua));
        });

        buttonDecrease.setOnClickListener(v -> {
            if (qua > 1) {
                qua--;
                product.setQuantity(qua);
                productQuantity.setText(String.valueOf(qua));
            } else {
                Toast.makeText(v.getContext(), "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = userID;

                product.setProductId(Id);
                product.setName(Name);
                int que = Integer.parseInt(productQuantity.getText().toString());
                product.setQuantity(que);
                product.setPrice(Price);
                product.setDescription(desc);
                product.setImageResource(Image_link);

                firestoreDB.collection("userCartList")
                        .whereEqualTo("user_id", userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        updateCart(document.getId(), product);
                                        return;
                                    }
                                    newCart(userId, product);
                                } else {

                                }
                            }
                        });
            }
        });
    }

    private void updateCart(String documentId, Product product) {
        DocumentReference docRef = firestoreDB.collection("userCartList").document(documentId);

        docRef.update("products", FieldValue.arrayUnion(getProductMap(product)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProductDetailsActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void newCart(String userId, Product productModel) {
        Map<String, Object> cartItem = new HashMap<>();
        List<Map<String, Object>> products = new ArrayList<>();

        products.add(getProductMap(productModel));
        cartItem.put("user_id", userId);
        cartItem.put("products", products);

        firestoreDB.collection("userCartList")
                .add(cartItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ProductDetailsActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public Map<String, Object> getProductMap(Product product) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("description", product.getDescription());
        productMap.put("image", product.getImageResource());
        productMap.put("price", product.getPrice());
        productMap.put("id", product.getProductId());
        productMap.put("name", product.getName());
        productMap.put("quantity", product.getQuantity());
        return productMap;
    }
}
