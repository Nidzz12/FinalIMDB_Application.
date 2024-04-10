package com.example.imdb_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PexelsActivity extends AppCompatActivity {

    private static final String API_KEY = "nUeIEFRrhNeUOzbu5dsPeMAZ78Sa8LIv3B4OgepoQWkQcmQLQGSLBPh1";
    private static final String BASE_URL = "https://api.pexels.com/v1/search?query=";
    private static final int MAX_SEARCH_HISTORY = 3;
    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerView;
    private PexelsAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pexels);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PexelsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        displaySearchHistory();
        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                showProgressBar();
                searchPhotos(query);
                saveSearchHistory(query);
            } else {
                Toast.makeText(PexelsActivity.this, "Enter a search term", Toast.LENGTH_SHORT).show();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PexelsActivity.this, PexelsFavoriteActivity.class);
                startActivity(intent);
            }
        });

        adapter.setOnImageClickListener(position -> {
            PexelsPhoto selectedPhoto = adapter.getItem(position);
            if (selectedPhoto != null) {
                Log.d("PexelsActivity", "Image clicked at position: " + position);
                Intent intent = new Intent(PexelsActivity.this, PexelDetailFragment.class);
                intent.putExtra("selectedPhoto", selectedPhoto);
                startActivity(intent);
            }
        });

    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void searchPhotos(String query) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + query)
                .addHeader("Authorization", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideProgressBar();
                    Toast.makeText(PexelsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    PexelsResponse pexelsResponse = gson.fromJson(responseData, PexelsResponse.class);
                    List<PexelsPhoto> photoList = pexelsResponse.getPhotos();

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        hideProgressBar(); // Move hideProgressBar() inside runOnUiThread()
                        adapter.setPhotoList(photoList);
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> {
                        hideProgressBar(); // Move hideProgressBar() inside runOnUiThread()
                        Toast.makeText(PexelsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private List<String> getSearchHistory(SharedPreferences sharedPreferences) {
        List<String> searchHistory = new ArrayList<>();
        int searchCount = sharedPreferences.getInt("SearchCount", 0);
        for (int i = 0; i < searchCount; i++) {
            searchHistory.add(sharedPreferences.getString("Search" + i, ""));
        }
        return searchHistory;
    }

    private void saveSearchHistory(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("pexelHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        List<String> searchHistory = getSearchHistory(sharedPreferences);

        searchHistory.add(0, title);

        if (searchHistory.size() > MAX_SEARCH_HISTORY) {
            searchHistory.remove(searchHistory.size() - 1);
        }

        for (int i = 0; i < searchHistory.size(); i++) {
            editor.putString("Search" + i, searchHistory.get(i));
        }
        editor.putInt("SearchCount", searchHistory.size());
        editor.apply();

        displaySearchHistory();
    }

    private void displaySearchHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("pexelHistory", MODE_PRIVATE);
        List<String> searchHistory = getSearchHistory(sharedPreferences);

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.removeAllViews();

        for (String search : searchHistory) {
            Chip chip = new Chip(this);
            chip.setText(search);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.chip_background_color);
            chip.setTextColor(getResources().getColor(android.R.color.black));
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressBar();
                    searchPhotos(search);

                }
            });
            chipGroup.addView(chip);
        }
    }
}
