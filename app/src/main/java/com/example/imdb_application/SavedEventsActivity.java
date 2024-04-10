package com.example.imdb_application;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SavedEventsActivity extends AppCompatActivity implements SavedEventAdapter.OnDeleteClickListener {
    private RecyclerView recyclerView;
    private SavedEventAdapter eventAdapter;
    private List<Event> eventList;
    private EventDao eventDao;
    private EventDatabase eventDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_events);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new SavedEventAdapter(eventList, this); // Pass the current activity as the listener
        recyclerView.setAdapter(eventAdapter);

        EventDatabase eventDatabase = Room.databaseBuilder(getApplicationContext(), EventDatabase.class, "event-database").build();
        eventDao = eventDatabase.eventDao();

        new FetchEventsTask().execute();
    }

    private class FetchEventsTask extends AsyncTask<Void, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Void... voids) {
            return eventDao.getAllEvents();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            if (events.isEmpty()) {
                View placeholderLayout = findViewById(R.id.layout_placeholder);
                if (placeholderLayout != null) {
                    placeholderLayout.setVisibility(View.VISIBLE);
                }
            } else {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        }
    }

    // Implement the method defined in SavedEventAdapter.OnDeleteClickListener interface
    @Override
    public void onDeleteClick(Event event) {
        deleteEvent(event);
    }

    private void deleteEvent(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        eventDao.delete(event);
                        runOnUiThread(() -> {
                            Snackbar.make(findViewById(android.R.id.content), "Event deleted successfully", Snackbar.LENGTH_SHORT).show();
                            eventList.remove(event); // Remove the event from the list
                            eventAdapter.notifyDataSetChanged(); // Notify the adapter of the change
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
