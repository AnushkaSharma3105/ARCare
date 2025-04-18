package com.example.arcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Medicine> cartList;
    private OnRemoveListener listener;

    // Interface to handle removal of an item
    public interface OnRemoveListener {
        void onRemove(Medicine medicine);
    }

    // Constructor updated to accept the OnRemoveListener
    public CartAdapter(ArrayList<Medicine> cartList, OnRemoveListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Medicine med = cartList.get(position);
        holder.name.setText(med.getName());
        holder.price.setText(med.getPrice());
        holder.quantity.setText("Quantity: " + med.getQuantity());

        // Set up remove button to call the listener
        holder.removeButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Medicine removedMedicine = cartList.get(pos);
                cartList.remove(pos);
                notifyItemRemoved(pos);
                listener.onRemove(removedMedicine); // Trigger the listener to remove from CartManager
                Toast.makeText(holder.itemView.getContext(), "Removed: " + removedMedicine.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up buy button
        holder.buyButton.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Buying: " + med.getName() + " x" + med.getQuantity(), Toast.LENGTH_SHORT).show();
            // Implement actual purchase logic here if needed
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    // ViewHolder to manage cart item layout
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        Button removeButton, buyButton;

        CartViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartMedicineName);
            price = itemView.findViewById(R.id.cartMedicinePrice);
            quantity = itemView.findViewById(R.id.cartMedicineQuantity);
            removeButton = itemView.findViewById(R.id.removeButton);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }
}
