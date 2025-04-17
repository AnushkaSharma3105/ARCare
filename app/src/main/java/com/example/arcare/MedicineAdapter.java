package com.example.arcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    interface OnBookClickListener {
        void onBookClick(Medicine medicine);
    }

    private ArrayList<Medicine> medicineList;
    private OnBookClickListener listener;

    public MedicineAdapter(ArrayList<Medicine> medicineList, OnBookClickListener listener) {
        this.medicineList = medicineList;
        this.listener = listener;
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
        holder.bookButton.setOnClickListener(v -> listener.onBookClick(med));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description;
        Button bookButton;

        MedicineViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medicineName);
            price = itemView.findViewById(R.id.medicinePrice);
            description = itemView.findViewById(R.id.medicineDescription);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }
}
