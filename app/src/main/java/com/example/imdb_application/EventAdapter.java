package com.example.imdb_application;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<String> eventNames;
    private JSONArray eventsArray;

    public EventAdapter(List<String> eventNames, JSONArray eventsArray) {
        this.eventNames = eventNames;
        this.eventsArray = eventsArray;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String eventName = eventNames.get(position);
        holder.textViewEventName.setText(eventName);

        holder.itemView.setOnClickListener(view -> {
            try {
                JSONObject event = eventsArray.getJSONObject(position);
                String eventDate = event.getJSONObject("dates").getJSONObject("start").getString("localDate");

                JSONArray priceRanges = event.optJSONArray("priceRanges"); // Use optJSONArray to handle null case
                String priceRange = "Price not available";
                if (priceRanges != null && priceRanges.length() > 0) {
                    String minPrice = priceRanges.getJSONObject(0).getString("min");
                    String maxPrice = priceRanges.getJSONObject(0).getString("max");
                    priceRange = minPrice + " - " + maxPrice + " CAD";
                }

                String imageUrl = event.getJSONArray("images").getJSONObject(0).getString("url");
                String ticketUrl = event.getString("url");

                // Launch EventDetailsActivity
                Intent intent = new Intent(holder.itemView.getContext(), EventDetail.class);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventDate", eventDate);
                intent.putExtra("priceRange", priceRange);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("ticketUrl", ticketUrl);
                holder.itemView.getContext().startActivity(intent);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException: " + e.getMessage());
            }
        });
    }



    @Override
    public int getItemCount() {
        return eventNames.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEventName;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textView_event_name);
        }
    }
}
