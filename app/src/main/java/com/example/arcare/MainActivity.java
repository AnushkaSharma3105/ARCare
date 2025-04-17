package com.example.arcare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.arcare.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button selectBtn, predictBtn, captureBtn, chatbotBtn, bookMedicineBtn;
    TextView result;
    ImageView imageView;
    Bitmap bitmap;
    List<String> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(MainActivity.this, SignUpLoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // ✅ Set toolbar as action bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getPermission();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                labels.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e("LabelError", "Error reading labels.txt", e);
        }

        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        captureBtn = findViewById(R.id.captureBtn);
        chatbotBtn = findViewById(R.id.chatbotBtn);
        bookMedicineBtn = findViewById(R.id.bookMedicineBtn);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

        selectBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10);
        });

        captureBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 12);
        });

        predictBtn.setOnClickListener(view -> {
            if (bitmap == null) {
                Toast.makeText(MainActivity.this, "Please select or capture an image first", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Model model = Model.newInstance(MainActivity.this);

                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, org.tensorflow.lite.DataType.FLOAT32);
                inputFeature0.loadBuffer(tensorImage.getBuffer());

                Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                float[] resultsArray = outputFeature0.getFloatArray();
                int maxIdx = getMax(resultsArray);

                if (maxIdx < labels.size()) {
                    result.setText("Prediction: " + labels.get(maxIdx));
                } else {
                    result.setText("Prediction index out of bounds: " + maxIdx);
                }

                model.close();
            } catch (IOException e) {
                Log.e("ModelError", "Error during prediction", e);
                Toast.makeText(MainActivity.this, "Error running model", Toast.LENGTH_SHORT).show();
            }
        });

        chatbotBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatbotActivity.class)));

        bookMedicineBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BookMedicineActivity.class)));
    }

    // 🔓 Logout menu integration
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            startActivity(new Intent(MainActivity.this, SignUpLoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == 10) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 12) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    int getMax(float[] arr) {
        int maxIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }
}
