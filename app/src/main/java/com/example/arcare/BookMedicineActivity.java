package com.example.arcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookMedicineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MedicineAdapter adapter;
    ArrayList<Medicine> medicineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_medicine);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        medicineList.add(new Medicine("Cetaphil Moisturizer", "₹300", "Soothes dry skin"));
        medicineList.add(new Medicine("Clindamycin Gel", "₹180", "Acne treatment"));
        medicineList.add(new Medicine("Hydrocortisone Cream", "₹120", "Anti-itch and inflammation"));
        medicineList.add(new Medicine("Neosporin Ointment", "₹200", "Heals cuts and skin infections"));

        adapter = new MedicineAdapter(medicineList);
        recyclerView.setAdapter(adapter);

        Button viewCartButton = findViewById(R.id.viewCartButton);
        viewCartButton.setOnClickListener(v -> {
            ArrayList<Medicine> cartItems = CartManager.getCart();
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(BookMedicineActivity.this, CartActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(BookMedicineActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
