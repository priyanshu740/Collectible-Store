package com.example.priyanshu_tripathi_p2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private int productId;
    private String name;
    private String description;
    private double price;
    private String imageResource;
    private int quantity;

    public Product(int productId, String name, String description, double price, String imageResource, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.quantity = quantity;
    }

    public Product(String name, String description, double price, String imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        // Default quantity
        this.quantity = 0;
    }

    protected Product(Parcel in) {
        productId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageResource = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Product() {

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageResource);
        dest.writeInt(quantity);
    }
}
