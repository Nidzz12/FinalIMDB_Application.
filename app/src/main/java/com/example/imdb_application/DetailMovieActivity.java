package com.example.imdb_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        String title = getIntent().getStringExtra("title");
        String year = getIntent().getStringExtra("year");
        String runtime = getIntent().getStringExtra("runtime");
        String actors = getIntent().getStringExtra("actors");
        String plot = getIntent().getStringExtra("plot");
        String posterUrl = getIntent().getStringExtra("posterUrl");

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView actorsTextView = findViewById(R.id.actorsTextView);
        TextView plotTextView = findViewById(R.id.plotTextView);
        ImageView imageView = findViewById(R.id.imageViewFav);
        FloatingActionButton floatingActionButton= findViewById(R.id.floatingActionButton);
        titleTextView.setText(title);
        actorsTextView.setText(actors);
        plotTextView.setText(plot);
        Glide.with(imageView.getContext())
                .load(posterUrl)
                .into(imageView);

        ChipGroup chipGroup = findViewById(R.id.chipsLayout);
        addChip(chipGroup, "Year", year);
        addChip(chipGroup, "Runtime", runtime);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start YourMoviesActivity
                Intent intent = new Intent(DetailMovieActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addChip(ChipGroup chipGroup, String label, String value) {
        Chip chip = new Chip(chipGroup.getContext());
        chip.setText(label + ": " + value);
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setChipBackgroundColorResource(R.color.chip_background_color);
        chip.setTextColor(getResources().getColor(android.R.color.black));
        chipGroup.addView(chip);
    }
}
