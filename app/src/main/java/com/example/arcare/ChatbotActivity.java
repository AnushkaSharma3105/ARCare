package com.example.arcare;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ChatbotActivity extends AppCompatActivity {

    private TextView chatOutput;
    private EditText userInput;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatOutput = findViewById(R.id.chatOutput);
        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(view -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                chatOutput.append("\nYou: " + input);
                String response = getBotResponse(input.toLowerCase());
                chatOutput.append("\nBot: " + response);
                userInput.setText("");
            }
        });
    }

    private String getBotResponse(String query) {
        if (query.contains("dust")) {
            return "Dust allergy symptoms: Sneezing, runny nose, itchy eyes.\nPrecautions: Use air purifiers, clean regularly.";
        } else if (query.contains("pollen")) {
            return "Pollen allergy symptoms: Itchy throat, watery eyes, congestion.\nPrecautions: Stay indoors during high pollen days.";
        } else if (query.contains("skin") || query.contains("rash")) {
            return "Skin allergy symptoms: Redness, itching, swelling.\nPrecautions: Avoid allergens, use prescribed ointments.";
        } else if (query.contains("pet")) {
            return "Pet allergy symptoms: Sneezing, wheezing, itchy eyes.\nPrecautions: Avoid close contact, clean pet areas often.";
        } else {
            return "Sorry, I don't have info on that. Try asking about dust, pollen, skin, or pet allergies.";
        }
    }
}