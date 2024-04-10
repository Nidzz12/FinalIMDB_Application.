package com.example.imdb_application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.UUID;

public class PexelDetailFragment extends AppCompatActivity {

    private ImageView imageView;
    private TextView dimensionsTextView, urlTextView;
    private PexelsPhotoDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pexel_detail);

        imageView = findViewById(R.id.imageViewFav);
        dimensionsTextView = findViewById(R.id.dimensionsTextView);
        urlTextView = findViewById(R.id.urlTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedPhoto")) {
            PexelsPhoto selectedPhoto = (PexelsPhoto) intent.getSerializableExtra("selectedPhoto");
            if (selectedPhoto != null) {
                displayPhotoDetails(selectedPhoto);
            } else {
                Toast.makeText(this, "Selected photo is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
        database = MyApplication.getInstance().getPexelsPhotoDatabase();

        // Find save button
        Button saveButton = findViewById(R.id.saveButton);

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePhotoToDatabase();
            }
        });
    }

    private void displayPhotoDetails(PexelsPhoto selectedPhoto) {
        // Display the image
        Glide.with(this)
                .load(selectedPhoto.getSrc().getMedium())
                .into(imageView);

        // Display dimensions
        String dimensions = getString(R.string.dimensions,
                selectedPhoto.getWidth(),
                selectedPhoto.getHeight());
        dimensionsTextView.setText(dimensions);


        String url = selectedPhoto.getUrl();
        urlTextView.setText(url);
        urlTextView.setOnClickListener(v -> openUrlInBrowser(url));
    }
    @SuppressLint("StaticFieldLeak")
    private void savePhotoToDatabase() {
        // Get selected photo details
        PexelsPhoto selectedPhoto = (PexelsPhoto) getIntent().getSerializableExtra("selectedPhoto");
        if (selectedPhoto != null) {
            // generating random ids
            UUID randomUUID = UUID.randomUUID();
            int randomId = randomUUID.hashCode();
            Pexels photoEntity = new Pexels(
                    randomId, // Use the generated random ID
                    selectedPhoto.getSrc().getMedium(),
                    selectedPhoto.getWidth(),
                    selectedPhoto.getHeight()
            );

            // Perform database operation asynchronously
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    // Insert photo details into database
                    database.pexelsPhotoDao().insert(photoEntity);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    // Show toast or perform other actions to indicate successful save
                    Toast.makeText(PexelDetailFragment.this, "Photo saved to database", Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
    }

    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
