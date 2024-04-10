package com.example.imdb_application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PexelsFavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PexelsPhotoDatabase database;
    private List<Pexels> favoritePhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_pexel);

        recyclerView = findViewById(R.id.pexelRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = MyApplication.getInstance().getPexelsPhotoDatabase();

        loadFavoritePhotos();
    }

    private void loadFavoritePhotos() {
        new AsyncTask<Void, Void, List<Pexels>>() {
            @Override
            protected List<Pexels> doInBackground(Void... voids) {
                // Retrieve saved photos from the database
                List<Pexels> retrievedPhotos = database.pexelsPhotoDao().getAll();

                return retrievedPhotos;
            }

            @Override
            protected void onPostExecute(List<Pexels> pexels) {
                super.onPostExecute(pexels);
                // Update UI with the list of saved photos
                favoritePhotos = pexels;
                recyclerView.setAdapter(new FavoritePhotosAdapter(favoritePhotos));
            }
        }.execute();
    }



    private void deletePhoto(Pexels photo) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Delete the photo from the database
                database.pexelsPhotoDao().delete(photo);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Notify user that photo has been deleted
                Snackbar.make(recyclerView, "Photo deleted", Snackbar.LENGTH_SHORT).show();
                // Refresh the list of favorite photos
                loadFavoritePhotos();
            }
        }.execute();
    }

    class FavoritePhotosAdapter extends RecyclerView.Adapter<FavoritePhotosAdapter.ViewHolder> {

        private List<Pexels> favoritePhotos;

        public FavoritePhotosAdapter(List<Pexels> favoritePhotos) {
            this.favoritePhotos = favoritePhotos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pexel, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pexels photo = favoritePhotos.get(position);
            Log.d("PexelsFavoriteActivity", "Retrieved photo: " + photo.getUrl().toString());
            Picasso.get().load(photo.getUrl()).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("Picasso", "Image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Picasso", "Error loading image", e);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PexelsFavoriteActivity.this)
                            .setTitle("Delete Photo")
                            .setMessage("Are you sure you want to delete this photo?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePhoto(photo);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return favoritePhotos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.pexelImageView);
            }
        }
    }
}
