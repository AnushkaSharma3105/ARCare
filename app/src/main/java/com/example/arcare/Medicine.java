package com.example.arcare;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicine implements Parcelable {
    private String name;
    private String price;
    private String description;
    private int quantity;

    // Constructor with quantity parameter
    public Medicine(String name, String price, String description, int quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;  // Set provided quantity
    }

    // Default constructor with default quantity of 1
    public Medicine(String name, String price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = 1; // default quantity
    }

    // Copy constructor to create a deep copy (used when adding to cart)
    public Medicine(Medicine medicine) {
        this.name = medicine.name;
        this.price = medicine.price;
        this.description = medicine.description;
        this.quantity = medicine.quantity;
    }

    protected Medicine(Parcel in) {
        name = in.readString();
        price = in.readString();
        description = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
