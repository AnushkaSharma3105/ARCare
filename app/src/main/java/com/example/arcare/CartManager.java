package com.example.arcare;

import java.util.ArrayList;

public class CartManager {
    private static ArrayList<Medicine> cart = new ArrayList<>();

    public static void addToCart(Medicine medicine) {
        cart.add(medicine);
    }

    public static ArrayList<Medicine> getCart() {
        return cart;
    }

    public static void clearCart() {
        cart.clear();
    }

    public static void removeFromCart(Medicine medicine) {
        cart.remove(medicine);
    }
}
