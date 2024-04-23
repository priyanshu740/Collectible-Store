package com.example.priyanshu_tripathi_p2;

import static android.widget.Toast.makeText;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.priyanshu_tripathi_p2.adapter.CartAdapter;
import com.example.priyanshu_tripathi_p2.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity  implements CartAdapter.CartItemClickListener{
    private CartAdapter cartAdapter;
    private LinearLayout cartItemsContainer;
    private TextView cartTitle,totalPriceText,textEmpty;
    private FirebaseAuth mAuth;
    RecyclerView recyclerAdapter;
    private DatabaseReference databaseReference;
    FirebaseFirestore firestoreDB;
    String userID;

    ArrayList<Product> cartDataList = new ArrayList<Product>();

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        totalPriceText = findViewById(R.id.totalPrice);

        textEmpty = findViewById(R.id.textEmpty);
        backButton = findViewById(R.id.backButton);
        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        cartTitle = findViewById(R.id.cartTitle);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestoreDB = FirebaseFirestore.getInstance();

        boolean clearCart = getIntent().getBooleanExtra("clearCart", false);
        if (clearCart) {
            clearCart();
            cartTitle.setText("Your cart is empty");
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getEmail();

            if (!userId.isEmpty()) {
                userID = userId;
                fetchCartData(userID);

            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartAdapter = new CartAdapter(cartDataList,this);


        recyclerAdapter = findViewById(R.id.recyclerAdapter);
        recyclerAdapter.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter.setHasFixedSize(true);
        cartAdapter = new CartAdapter(cartDataList,this);
        recyclerAdapter.setAdapter(cartAdapter);

        ImageView deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartAdapter != null) {
                    cartAdapter.clearCart();
                    cartTitle.setText("Your cart is empty");
                }
            }
        });

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            if (cartDataList == null || cartDataList.isEmpty()) {
                makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);

            }
        });
    }

    public void clearCart() {
        cartItemsContainer.removeAllViews();
    }

    private void fetchCartData(String userId) {
        firestoreDB.collection("userCartList")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                                for (Map<String, Object> product : products) {
                                    int id = ((Long) product.get("id")).intValue();
                                    String specifications = (String) product.get("description");
                                    String image_link = (String) product.get("image");
                                    int quantityValue = ((Long) product.get("quantity")).intValue();

                                    String name = (String) product.get("name");
                                    double price = (double) product.get("price");
                                    Product cartList = new Product(id, name, specifications, price, image_link,quantityValue);
                                    cartList.setQuantity((int) quantityValue);
                                    cartList.setDescription(specifications);
                                    cartList.setImageResource(image_link);
                                    cartList.setPrice(price);
                                    cartList.setProductId(id);
                                    cartList.setName(name);

                                    cartDataList.add(cartList);
                                    cartAdapter.notifyDataSetChanged();

                                    double totalPrice = 0.0;

                                    for (Product cartItem1 : cartDataList) {
                                        totalPrice += cartItem1.getPrice() * cartItem1.getQuantity();
                                    }

                                    String formattedTotalPrice = String.format("%.2f", totalPrice);

                                    totalPriceText.setText(("Cart Total : $ "+String.valueOf(formattedTotalPrice)));

                                }
                                return;
                            }
                        }
                    }
                });
    }


    @Override
    public void onIncreaseButtonClick(Product product) {
        double totalPrice = cartAdapter.getPrice();
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        totalPriceText.setText("Cart Total : $" + String.valueOf(formattedTotalPrice));
    }

    @Override
    public void onDecreaseButtonClick(Product product) {
        double totalPrice = cartAdapter.getPrice();
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        totalPriceText.setText("Cart Total : $" + String.valueOf(formattedTotalPrice));
    }

    @Override
    public void onItemDelete(Product product) {
        double totalPrice = cartAdapter.getPrice();
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        totalPriceText.setText("Cart Total : $" + String.valueOf(formattedTotalPrice));

        removeProductFromCart(userID, product.getProductId());


        if(cartDataList.isEmpty()){
            textEmpty.setVisibility(View.VISIBLE);
            totalPriceText.setVisibility(View.GONE);
            recyclerAdapter.setVisibility(View.GONE);
        }else {
            textEmpty.setVisibility(View.GONE);
            totalPriceText.setVisibility(View.VISIBLE);
            recyclerAdapter.setVisibility(View.VISIBLE);
        }
    }

    public void removeProductFromCart(String userId, int productId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCartCollectionRef = db.collection("userCartList");
        Query query = userCartCollectionRef.whereEqualTo("user_id", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Map<String, Object>> cartProducts = (List<Map<String, Object>>) document.get("products");

                        for (Iterator<Map<String, Object>> iterator = cartProducts.iterator(); iterator.hasNext(); ) {
                            Map<String, Object> product = iterator.next();
                            int id = Math.toIntExact((Long) product.get("id"));
                            if (id == productId) {
                                iterator.remove();
                                cartAdapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        document.getReference().update("products", cartProducts)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Product removed from cart");

                                            // Perform any additional actions if needed
                                        } else {
                                            Log.e(TAG, "Failed to remove product from cart", task.getException());
                                        }
                                    }
                                });
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

}