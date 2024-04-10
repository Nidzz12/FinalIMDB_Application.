package com.example.imdb_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

public class EventDetail extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_event_detail);

                // Get data passed from previous activity
                Intent intent = getIntent();
                String eventName = intent.getStringExtra("eventName");
                String eventDate = intent.getStringExtra("eventDate");
                String priceRange = intent.getStringExtra("priceRange");
                String imageUrl = intent.getStringExtra("imageUrl");
                String ticketUrl = intent.getStringExtra("ticketUrl");

                // Set up views
                TextView eventNameTextView = findViewById(R.id.textView_event_name);
                Chip eventDateChip = findViewById(R.id.chip_event_date);
                Chip priceRangeChip = findViewById(R.id.chip_event_price_range);
                ImageView eventImageView = findViewById(R.id.imageView_event);
                Button buyTicketsButton = findViewById(R.id.button_buy_tickets);

                // Set data to views
                eventNameTextView.setText(eventName);
                eventDateChip.setText(eventDate);
                priceRangeChip.setText(priceRange); // Set text to chip
                Glide.with(this).load(imageUrl).into(eventImageView);

                // Create an Event object
                Event event = new Event(eventName, eventDate, priceRange, imageUrl, ticketUrl);

                buyTicketsButton.setOnClickListener(view -> {
                        // Click to open in the browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ticketUrl));
                        startActivity(browserIntent);
                });

                Button saveButton = findViewById(R.id.button_save_to_database);

                saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                                MyApplication.getEventDatabase().eventDao().insertEvent(event);
                                                // Use runOnUiThread to update UI components from a background thread
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                                // Display a toast message on the UI thread
                                                                Toast.makeText(EventDetail.this, "Event saved to database", Toast.LENGTH_SHORT).show();
                                                        }
                                                });
                                        }
                                }).start();
                        }
                });
        }

}
