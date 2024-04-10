package com.example.imdb_application;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {


    private EditText titleEditText;
    private Button searchButton;
    private TextView resultTextView;
    private TextView searchHistoryTextView;
    private TextView titleTextView;
    private TextView yearTextView;
    private TextView ratingTextView;
    private TextView runtimeTextView;
    private TextView actorsTextView;
    private TextView plotTextView;
    private JSONObject lastApiResponse;
    private TextView posterUrlTextView;

    private RequestQueue requestQueue;
    // API URL WITH KEY
    private static final String BASE_URL = "https://omdbapi.com/?apikey=6c9862c2&t=";
    private static final int MAX_SEARCH_HISTORY =3;
    private MovieDatabase movieDatabase;

    private FloatingActionButton savedMovies;
    private ImageView posterImageView;
    private ProgressBar loader;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.titleEditText);
        searchButton = findViewById(R.id.searchButton);
        searchHistoryTextView = findViewById(R.id.previousSearchesTextView);
        titleTextView = findViewById(R.id.titleTextView);
        yearTextView = findViewById(R.id.yearTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        runtimeTextView = findViewById(R.id.runtimeTextView);
        actorsTextView = findViewById(R.id.actorsTextView);
        plotTextView = findViewById(R.id.plotTextView);
        posterUrlTextView = findViewById(R.id.posterUrlTextView);
        Button saveButton = findViewById(R.id.saveButton);
        requestQueue = Volley.newRequestQueue(this);
        posterImageView = findViewById(R.id.posterImageView);
        savedMovies =findViewById(R.id.savedMovies);
        loader = findViewById(R.id.loader);
        movieDatabase = Room.databaseBuilder(getApplicationContext(),
                MovieDatabase.class, "movie-database").build();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                if (!title.isEmpty()) {
                    loader.setVisibility(View.VISIBLE);
                    searchMovie(title);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovieDetails();
            }
        });
        savedMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start YourMoviesActivity
                Intent intent = new Intent(MainActivity.this, YourMoviesActivity.class);
                startActivity(intent);
            }
        });
        // Display the search history
        displaySearchHistory();
    }

    private void searchMovie(String title) {
        // Save the search history
        saveSearchHistory(title);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + title, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                lastApiResponse = response;
                try {
                    String movieTitle = response.getString("Title");
                    String year = response.getString("Year");
                    String rating = response.getString("imdbRating");
                    String runtime = response.getString("Runtime");
                    String actors = response.getString("Actors");
                    String plot = response.getString("Plot");
                    String posterUrl = response.getString("Poster");
                    titleTextView.setText(movieTitle);
                    yearTextView.setText( year);
                    ratingTextView.setText(rating);
                    runtimeTextView.setText( runtime);
                    actorsTextView.setText(actors);
                    plotTextView.setText( plot);
                    posterUrlTextView.setText(posterUrl);
                    Picasso.get().load(posterUrl).into(posterImageView);
                    loader.setVisibility(View.GONE);
                    findViewById(R.id.movie_information_card).setVisibility(View.VISIBLE);
                    findViewById(R.id.movie_placeholder).setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();

                    loader.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error fetching movie data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }


    private void saveSearchHistory(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        List<String> searchHistory = getSearchHistory(sharedPreferences);

        searchHistory.add(0, title);

        // Limit the search history to 3 entries
        if (searchHistory.size() > MAX_SEARCH_HISTORY) {
            searchHistory.remove(searchHistory.size() - 1);
        }

        // Save the updated search history
        for (int i = 0; i < searchHistory.size(); i++) {
            editor.putString("Search" + i, searchHistory.get(i));
        }
        editor.putInt("SearchCount", searchHistory.size());
        editor.apply();

        // Update the displayed search history
        displaySearchHistory();
    }

    private void saveMovieDetails() {
        // Get movie details from TextViews
        String title = titleTextView.getText().toString();
        String year = yearTextView.getText().toString();
        String runtime = runtimeTextView.getText().toString();
        String actors = actorsTextView.getText().toString();
        String plot = plotTextView.getText().toString();
        String posterUrl = posterUrlTextView.getText().toString();
        String rating = ratingTextView.getText().toString();
        String language = "";
        String director = "";
        if (lastApiResponse != null) {
            try {
                language = lastApiResponse.getString("Language");
                director = lastApiResponse.getString("Director");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("ss",posterUrl);
        // Check if any of the fields is empty
        if (title.isEmpty() || year.isEmpty() || runtime.isEmpty() || actors.isEmpty() || plot.isEmpty() || posterUrl.isEmpty()) {
            Toast.makeText(this, "Please fetch movie details first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Movie object with the retrieved details
        Movie movie = new Movie(title, year, runtime, actors, plot, posterUrl, rating, language, director);

        new Thread(new Runnable() {
            @Override
            public void run() {
                movieDatabase.movieDao().insert(movie);
            }
        }).start();

        Toast.makeText(this, "Movie details saved successfully", Toast.LENGTH_SHORT).show();
    }


    private List<String> getSearchHistory(SharedPreferences sharedPreferences) {
        List<String> searchHistory = new ArrayList<>();
        int searchCount = sharedPreferences.getInt("SearchCount", 0);
        for (int i = 0; i < searchCount; i++) {
            searchHistory.add(sharedPreferences.getString("Search" + i, ""));
        }
        return searchHistory;
    }

    private void displaySearchHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE);
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
                    // Call searchMovie() with the title associated with the clicked chip
                    searchMovie(search);
                }
            });
            chipGroup.addView(chip);
        }
    }
}
