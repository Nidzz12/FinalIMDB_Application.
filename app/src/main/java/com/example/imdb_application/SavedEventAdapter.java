package com.example.imdb_application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.List;

public class SavedEventAdapter extends RecyclerView.Adapter<SavedEventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnDeleteClickListener onDeleteClickListener;

    public SavedEventAdapter(List<Event> eventList, OnDeleteClickListener onDeleteClickListener) {
        this.eventList = eventList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDateTextView.setText(event.getEventDate());
        holder.priceRangeTextView.setText(event.getPriceRange());
        Glide.with(holder.itemView.getContext())
                .load(event.getImageUrl())
                .into(holder.imageView);
        holder.ticketUrlTextView.setTag(event.getTicketUrl());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        Chip eventDateTextView;
        Chip priceRangeTextView;
        TextView ticketUrlTextView;
        ImageView imageView;
        Button deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            priceRangeTextView = itemView.findViewById(R.id.chip_price_range);
            imageView = itemView.findViewById(R.id.event_image);
            ticketUrlTextView = itemView.findViewById(R.id.ticket_url);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Event event);
    }
}
