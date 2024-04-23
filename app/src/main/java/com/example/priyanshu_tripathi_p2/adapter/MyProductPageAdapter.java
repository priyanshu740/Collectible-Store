package com.example.priyanshu_tripathi_p2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.priyanshu_tripathi_p2.R;
import com.example.priyanshu_tripathi_p2.model.Product;

import java.util.ArrayList;

public class MyProductPageAdapter extends RecyclerView.Adapter<MyProductPageAdapter.MyViewHolder> {

    ArrayList<Product> productList = new ArrayList<>();

    private OnItemClickListener listener;

    public MyProductPageAdapter(ArrayList<Product> myDataSet, OnItemClickListener listener){
        productList = myDataSet;
        this.listener = listener;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutMain;
        private ImageView productImages;
        private TextView productName;
        private TextView productPrice;
        private TextView productDescription;
        private Button addToCartButton;
        public MyViewHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.product_item,parent,false));


            productImages = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            layoutMain = itemView.findViewById(R.id.layoutMain);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product productModel);
        void onAddToCartClick(Product productModel, int quantity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.productName.setText(productList.get(position).getName());
        double price = productList.get(position).getPrice();

//        holder.productPrice.setText(Double.toString(price));
        holder.productPrice.setText("$"+Double.toString(price));
//        holder.txtProductPrice.setText(productList.get(position).getPrice());
//        holder.txtProductPrice.setText("Cost : $" + productList.get(position).getPrice());
        holder.productDescription.setText(productList.get(position).getDescription());

        String imageLink = productList.get(position).getImageResource();
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(holder.itemView.getContext())
                .load(imageLink)
                .apply(requestOptions)
                .into(holder.productImages);

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(productList.get(position));
                }
            }
        });

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAddToCartClick(productList.get(position) , 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
