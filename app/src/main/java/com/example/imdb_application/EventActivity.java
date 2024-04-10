package com.example.imdb_application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();

    private EditText cityEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private static final int MAX_SEARCH_HISTORY = 3;
    private List<String> eventNames;

    private JSONArray eventsArray;

private TextView textHint;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        cityEditText = findViewById(R.id.editText_city);
        searchButton = findViewById(R.id.button_search);
        recyclerView = findViewById(R.id.recyclerView_events);
        textHint = findViewById(R.id.text_hint);

        eventNames = new ArrayList<>();
        eventAdapter = new EventAdapter(eventNames, eventsArray);

        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displaySearchHistory();
        searchButton.setOnClickListener(view -> searchEvents());
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, SavedEventsActivity.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void searchEvents() {
        String cityName = cityEditText.getText().toString().trim();
        String apiKey = "3KAkecnabfke7351V9uWMCf5SHne0GCy";
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + apiKey + "&city=" + cityName + "&radius=100&size=20";
        saveSearchHistory(cityName);
        if (cityName.isEmpty()) {
            textHint.setVisibility(View.VISIBLE);
            return;
        } else {

            textHint.setVisibility(View.GONE);
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss(); // Dismiss loader

                        try {
                            eventsArray = response.getJSONObject("_embedded").getJSONArray("events");
                            eventNames.clear();
                            for (int i = 0; i < eventsArray.length(); i++) {
                                JSONObject event = eventsArray.getJSONObject(i);
                                String eventName = event.getString("name");
                                eventNames.add(eventName);
                            }
                            eventAdapter = new EventAdapter(eventNames, eventsArray); // Pass both eventNames and eventsArray to the adapter
                            recyclerView.setAdapter(eventAdapter);
                            eventAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss(); // Dismiss loader
                        Log.e(TAG, "Error fetching events: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
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
        SharedPreferences sharedPreferences = getSharedPreferences("eventHistory", MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = getSharedPreferences("eventHistory", MODE_PRIVATE);
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
                    String searchText = ((Chip) v).getText().toString();
                    cityEditText.setText(searchText);
                    searchEvents();
                }
            });
            chipGroup.addView(chip);
        }
    }

}
