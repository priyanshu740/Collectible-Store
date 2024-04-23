package com.example.priyanshu_tripathi_p2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.priyanshu_tripathi_p2.adapter.MyProductPageAdapter;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity implements MyProductPageAdapter.OnItemClickListener {

    private RecyclerView productRecyclerView;
    private MyProductPageAdapter myProductPageAdapter;
    private ImageButton cartButton,logout;
    private TextView cartCountTextView;
    private List<Product> cartList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    String userID;
    FirebaseFirestore firestoreDB;
    ImageView backButton;
    FirebaseFirestore firebaseFirestoreDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestoreDB = FirebaseFirestore.getInstance();
        backButton = findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getEmail();
            if (!userId.isEmpty()) {
                userID = userId;
            }
        }

        logout = findViewById(R.id.logout);
        cartButton = findViewById(R.id.cartButton);
        cartCountTextView = findViewById(R.id.cartCountTextView);

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        cartButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
                intent.putParcelableArrayListExtra("cartList", new ArrayList<>(cartList));
                startActivity(intent);
        });

        fetchProductsFromDatabase();
    }

    private void fetchProductsFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Product> productList = new ArrayList<>();
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        try {
                            int quantity = productSnapshot.child("quantity").getValue(Integer.class);
                            int productId = productSnapshot.child("id").getValue(Integer.class);
                            String name = productSnapshot.child("name").getValue(String.class);
                            String description = productSnapshot.child("description").getValue(String.class);
                            double price = productSnapshot.child("price").getValue(Double.class);
                            String image = productSnapshot.child("image").getValue(String.class);

//                              String formattedPrice = "$" + price;
//                            productList.add(new Product(name, description, price, image));
                            productList.add(new Product(productId, name, description, price, image,quantity));

                            Log.e("productList", "" + productList);

                            Log.d("Firebase", "Product fetched - Name: " + name + ", Description: " + description + ", Price: " + price + ", ImageURL: " + image);
                        } catch (Exception e) {
                            Log.e("ProductList1Activity", "Error parsing snapshot: " + e.getMessage());
                        }
                    }
                    myProductPageAdapter = new MyProductPageAdapter(productList, ProductListActivity.this);
//                    productAdapter.setOnAddToCartClickListener(ProductList1Activity.this);
                    productRecyclerView.setAdapter(myProductPageAdapter);
                } else {
                    Log.e("ProductList1Activity", "No products found in the database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProductList1Activity", "Error fetching products: " + databaseError.getMessage());
            }
        });
    }





    private void updateCartCount() {
        int cartItemCount = cartList.size();
        cartCountTextView.setText("Cart (" + cartItemCount + ")");

        // Enable/disable cart button based on cart items
        cartButton.setEnabled(cartItemCount > 0);
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
        intent.putExtra("Id", product.getProductId());
        intent.putExtra("Name", product.getName());
        intent.putExtra("Price", product.getPrice());
        intent.putExtra("Quantity", 1);
        intent.putExtra("Image_link", product.getImageResource());
        intent.putExtra("Specifications", product.getDescription());
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product, int quantity) {
        cartList.add(product);
        updateCartCount();

        final String userId = userID;

        product.setProductId(product.getProductId());
        product.setName(product.getName());
        product.setQuantity(Integer.parseInt(String.valueOf(1)));
        product.setPrice(product.getPrice());
        product.setDescription(product.getDescription());
        product.setImageResource(product.getImageResource());

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

    private void updateCart(String documentId, Product product) {
        DocumentReference docRef = firestoreDB.collection("userCartList").document(documentId);

        docRef.update("products", FieldValue.arrayUnion(getProductMap(product)))
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
                        Toast.makeText(ProductListActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();

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
