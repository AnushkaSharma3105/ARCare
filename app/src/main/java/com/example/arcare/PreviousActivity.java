package com.example.arcare;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreviousActivity extends AppCompatActivity {

    ListView previousImagesListView;
    List<PreviousImage> previousImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous);

        previousImagesListView = findViewById(R.id.previousImagesListView);
        previousImageList = new ArrayList<>();

        // Load image paths from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        Set<String> paths = prefs.getStringSet("capturedImages", new HashSet<>());

        // Ensure paths are not null or empty
        if (paths != null && !paths.isEmpty()) {
            for (String path : paths) {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    // Decode the image file into a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bitmap != null) {
                        // Add PreviousImage to the list
                        previousImageList.add(new PreviousImage(bitmap, imgFile.getName()));
                    }
                }
            }

            // Now initialize the adapter with the populated list
            if (!previousImageList.isEmpty()) {
                PreviousImageAdapter adapter = new PreviousImageAdapter(this, previousImageList);
                previousImagesListView.setAdapter(adapter);
            } else {
                // Handle case where no images were loaded
                // Show a message or a placeholder view
            }
        }
    }
}
