package com.example.arcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private ArrayList<Medicine> medicineList;

    public MedicineAdapter(ArrayList<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine med = medicineList.get(position);
        holder.name.setText(med.getName());
        holder.price.setText(med.getPrice());
        holder.description.setText(med.getDescription());

        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                new Integer[]{1, 2, 3, 4, 5});
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.quantitySpinner.setAdapter(quantityAdapter);

        holder.addToCartButton.setOnClickListener(v -> {
            int qty = Integer.parseInt(holder.quantitySpinner.getSelectedItem().toString());
            Medicine medicineWithQty = new Medicine(med.getName(), med.getPrice(), med.getDescription(), qty);
            CartManager.addToCart(medicineWithQty);
            Toast.makeText(v.getContext(), med.getName() + " added to cart (" + qty + ")", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description;
        Spinner quantitySpinner;
        Button addToCartButton;

        MedicineViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medicineName);
            price = itemView.findViewById(R.id.medicinePrice);
            description = itemView.findViewById(R.id.medicineDescription);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            quantitySpinner = itemView.findViewById(R.id.quantitySpinner);
        }
    }
}
