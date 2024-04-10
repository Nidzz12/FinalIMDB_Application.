package com.example.imdb_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class YourMoviesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private MovieDatabase movieDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_movies);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);

        movieDatabase = MovieDatabase.getInstance(this);

        loadSavedMovies();
    }

    private void loadSavedMovies() {

        new Thread(() -> {
            List<Movie> savedMovies = movieDatabase.movieDao().getAllMovies();

            for (Movie movie : savedMovies) {
                Log.d("Rating", "Title: " + movie.getRating());
                Log.d("MovieDetails", "Year: " + movie.getYear());
                Log.d("MovieDetails", "Runtime: " + movie.getRuntime());

            }
            runOnUiThread(() -> adapter.setMovies(savedMovies));
        }).start();
    }


    private void deleteMovie(Movie movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        movieDatabase.movieDao().delete(movie);

                        runOnUiThread(() -> {
                            loadSavedMovies();
                            Snackbar.make(findViewById(android.R.id.content), "Movie deleted successfully", Snackbar.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }


    private class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

        private List<Movie> movies;

        public void setMovies(List<Movie> movies) {
            this.movies = movies;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            Movie movie = movies.get(position);
            holder.bind(movie);
        }

        @Override
        public int getItemCount() {
            return movies != null ? movies.size() : 0;
        }

        class MovieViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView titleTextView;
            private TextView detailsTextView;
            private TextView ratingTextView;
            private TextView actorsTextView;
            private Button deleteButton;

            public MovieViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageViewFav);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                detailsTextView = itemView.findViewById(R.id.detailsTextView);
                ratingTextView = itemView.findViewById(R.id.ratingTextView);
                actorsTextView = itemView.findViewById(R.id.actorsTextView);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }

            public void bind(Movie movie) {

                Glide.with(imageView.getContext())
                        .load(movie.getPosterUrl())
                        .into(imageView);
                Log.d("wsd","picc"+movie.getPosterUrl());
                Log.d("MovieDetails", "Glide: " + movie.getPosterUrl());
                titleTextView.setText(movie.getTitle());
                String details = movie.getYear() + " | " + movie.getRuntime() + " | ";
                detailsTextView.setText(details);
                actorsTextView.setText("Actors: " + movie.getActors());
                deleteButton.setOnClickListener(v -> deleteMovie(movie));
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), DetailMovieActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("year", movie.getYear());
                    intent.putExtra("runtime", movie.getRuntime());
                    intent.putExtra("actors", movie.getActors());
                    intent.putExtra("plot", movie.getPlot());
                    intent.putExtra("posterUrl",movie.getPosterUrl());
                    itemView.getContext().startActivity(intent);
                });
            }
        }
    }
}
