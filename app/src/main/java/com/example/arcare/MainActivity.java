package com.example.arcare;

import android.util.Log;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.arcare.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button selectBtn, predictBtn, captureBtn, chatbotBtn, bookMedicineBtn;
    TextView result;
    ImageView imageView, profileIcon;
    Bitmap bitmap;
    List<String> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(MainActivity.this, SignUpLoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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
        profileIcon = findViewById(R.id.profileIcon);

        profileIcon.setOnClickListener(v -> showUserProfileDialog());

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
                Log.d("MyApp", "Model Output: 120 ");
                TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
                Log.d("MyApp", "Model Output: 122");
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, org.tensorflow.lite.DataType.FLOAT32);
                Log.d("MyApp", "Model Output: 124");
                inputFeature0.loadBuffer(tensorImage.getBuffer());
                Log.d("MyApp", "Model Output: 126");
                Model.Outputs outputs = model.process(inputFeature0);
                Log.d("MyApp", "Model Output: 128 ");
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                Log.d("MyApp", "Model Output:130 ");

                float[] resultsArray = outputFeature0.getFloatArray();
                Log.d("MyApp", "Model Output: " + resultsArray[0]);
                int maxIdx = getMax(resultsArray);
                System.out.println("134");

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
            saveImageToStorage(bitmap);
        }
    }

    private void saveImageToStorage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "CapturedImages");
        if (!directory.exists()) {
            directory.mkdir();
        }

        String filename = "IMG_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            Set<String> paths = prefs.getStringSet("capturedImages", new HashSet<>());
            paths.add(file.getAbsolutePath());
            prefs.edit().putStringSet("capturedImages", paths).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int getMax(float[] arr) {
        int maxIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }

    private void showUserProfileDialog() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "Anushka");
        String email = prefs.getString("userEmail", "anushka@example.com");

        String userInfo = "Name: " + name + "\nEmail: " + email;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Profile");
        builder.setMessage(userInfo);

        builder.setPositiveButton("Previous Activity", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, PreviousActivity.class));
            }
        });

        builder.setNegativeButton("Close", null);
        builder.show();
    }
}
