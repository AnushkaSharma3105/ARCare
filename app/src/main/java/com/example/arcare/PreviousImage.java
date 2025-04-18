package com.example.arcare;

import android.graphics.Bitmap;

public class PreviousImage {
    private Bitmap image;
    private String timestamp;

    // Constructor
    public PreviousImage(Bitmap image, String timestamp) {
        this.image = image;
        this.timestamp = timestamp;
    }

    // Getters
    public Bitmap getImage() {
        return image;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
