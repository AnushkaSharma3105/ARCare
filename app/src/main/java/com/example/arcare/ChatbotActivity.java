package com.example.arcare;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class ChatbotActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    EditText userInput;
    Button sendBtn, speakBtn, micBtn;
    TextView responseText;
    TextToSpeech tts;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);
        micBtn = findViewById(R.id.micBtn);
        speakBtn = findViewById(R.id.speakBtn);
        responseText = findViewById(R.id.response);

        // Initialize TextToSpeech
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR)
                tts.setLanguage(Locale.UK);
        });

        // Request microphone permission at runtime
        checkAudioPermission();

        sendBtn.setOnClickListener(view -> {
            String query = userInput.getText().toString().trim();
            if (!query.isEmpty()) {
                getBotResponse(query);
            }
        });

        speakBtn.setOnClickListener(view -> {
            String text = responseText.getText().toString();
            if (!text.isEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        micBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(ChatbotActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startVoiceInput();
            } else {
                Toast.makeText(this, "Permission denied. Cannot record audio.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAudioPermission() {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        startActivityForResult(intent, 100);
    }

    private void getBotResponse(String query) {
        responseText.setText("⏳ Thinking...");

        String json = "{\n" +
                "  \"model\": \"deepseek/deepseek-r1:free\",\n" +
                "  \"messages\": [{ \"role\": \"user\", \"content\": \"" + query + "\" }]\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseText.setText("❌ Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        JSONArray choices = obj.getJSONArray("choices");
                        String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");

                        runOnUiThread(() -> {
                            responseText.setText("🤖 ARCare Bot says:\n" + reply.trim());
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> responseText.setText("⚠️ Parsing error."));
                    }
                } else {
                    runOnUiThread(() -> responseText.setText("❌ Server error."));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            userInput.setText(result.get(0));
            getBotResponse(result.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
