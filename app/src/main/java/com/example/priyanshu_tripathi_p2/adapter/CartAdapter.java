package com.example.priyanshu_tripathi_p2.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.priyanshu_tripathi_p2.R;
import com.example.priyanshu_tripathi_p2.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Product> cartList;
    private CartItemClickListener itemClickListener;
    private Map<Integer, Long> quantityMap;
    int qua = 1;
    Product product;
    public CartAdapter(List<Product> cartList, CartItemClickListener itemClickListener) {
        this.cartList = cartList;
        this.itemClickListener = itemClickListener;
        quantityMap = new HashMap<>();
        for (int i = 0; i < cartList.size(); i++) {
            quantityMap.put(i, 1L);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        product = cartList.get(position);
        holder.productName.setText(product.getName());
//        holder.productPrice.setText("$" + product.getPrice());
        holder.productDescription.setText(product.getDescription());
        holder.productQuantity.setText(""+product.getQuantity());

        qua = product.getQuantity();
        double price = product.getPrice();

        double totalPrice = price * qua;

        holder.productPrice.setText(String.format("$%.2f", totalPrice));

        holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qua = cartList.get(position).getQuantity();
                qua++;
                product.setQuantity(qua);
                holder.productQuantity.setText(String.valueOf(qua));


                if (itemClickListener != null) {
                    itemClickListener.onIncreaseButtonClick(product);
                }

            }
        });

        holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qua = cartList.get(position).getQuantity();
                if (qua > 1) {
                    qua--;
                    product.setQuantity(qua);
                    holder.productQuantity.setText(String.valueOf(qua));

                    if (itemClickListener != null) {
                        itemClickListener.onDecreaseButtonClick(product);
                    }
                } else {
                    Toast.makeText(v.getContext(), "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }

            }
        });

        String imageLink = cartList.get(position).getImageResource();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(holder.itemView.getContext())
                .load(imageLink)
                .apply(requestOptions)
                .into(holder.productImage);

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
//                    product = cartList.get(position);
                    if (position >= 0 && position < cartList.size()) {
                        cartList.remove(position);
                        notifyItemRemoved(position);

                        itemClickListener.onItemDelete(product);
                    }

                }
            }
        });

    }
    public double getPrice() {
        double totalPrice = 0.0;
        for (Product cartItem : cartList) {
            totalPrice += (cartItem.getPrice() * cartItem.getQuantity());
        }
        return totalPrice;
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription, productQuantity;
        Button buttonIncrease, buttonDecrease,btnRemove;

        ImageView productImage;

        @SuppressLint("WrongViewCast")
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);

        }

    }

    public interface CartItemClickListener {
        void onIncreaseButtonClick(Product product);
        void onDecreaseButtonClick(Product product);
        void onItemDelete(Product product);
    }

    public void clearCart() {
        cartList.clear();
        notifyDataSetChanged();
    }
}