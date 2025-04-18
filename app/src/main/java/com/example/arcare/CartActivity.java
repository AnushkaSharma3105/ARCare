package com.example.arcare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;
    ArrayList<Medicine> cartList;
    TextView emptyCartText;
    Button buyNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        emptyCartText = findViewById(R.id.emptyCartText);
        buyNowButton = findViewById(R.id.buyNowButton);

        cartList = CartManager.getCart();  // Get cart from shared manager

        if (cartList == null || cartList.isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            buyNowButton.setVisibility(View.GONE);
        } else {
            emptyCartText.setVisibility(View.GONE);
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartAdapter = new CartAdapter(cartList, medicine -> {
                CartManager.removeFromCart(medicine);
                cartAdapter.notifyDataSetChanged();

                if (CartManager.getCart().isEmpty()) {
                    emptyCartText.setVisibility(View.VISIBLE);
                    buyNowButton.setVisibility(View.GONE);
                }
            });
            cartRecyclerView.setAdapter(cartAdapter);

            buyNowButton.setOnClickListener(v -> {
                Toast.makeText(CartActivity.this, "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
                CartManager.clearCart();
                finish(); // Optionally go back to medicine list
            });
        }
    }
}
