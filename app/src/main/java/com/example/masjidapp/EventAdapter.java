package com.example.masjidapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<EventModel> eventList;
    private OnEventUpdateClickListener updateClickListener;
    private OnEventDeleteClickListener deleteClickListener;

    public EventAdapter(Context context, List<EventModel> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);

        // Bind data to the views
        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(event.getDate());
        holder.eventType.setText(event.getType());
        String combinedTime = event.getStartTime() + " - " + event.getEndTime() + " WIB";
        holder.eventStartTime.setText(combinedTime);
        holder.eventDescription.setText(event.getDescription());

        // Load event image with Glide
        if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(event.getImageUri()))
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_image); // default image if no image URL
        }

        // Handle click to navigate to EventDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("EVENT_DETAIL", event);  // Passing the event data to EventDetailActivity
            context.startActivity(intent);  // Start the activity
        });

        // Handle update button click
        holder.btnUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                updateClickListener.onEventUpdateClick(event, position);  // Trigger event update
            }
        });

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onEventDeleteClick(position);  // Trigger event delete
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Method to update event list
    public void setEvents(List<EventModel> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged(); // Notify adapter that data has changed
    }

    // Method to add a new event to the list
    public void addEvent(EventModel event) {
        eventList.add(event);
        notifyItemInserted(eventList.size() - 1);
    }

    // Method to remove an event from the list
    public void removeEvent(int position) {
        eventList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventList.size());
    }

    // Set the listener for event updates
    public void setOnEventUpdateClickListener(OnEventUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    // Set the listener for event deletes
    public void setOnEventDeleteClickListener(OnEventDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // Interface for update event click
    public interface OnEventUpdateClickListener {
        void onEventUpdateClick(EventModel event, int position);
    }

    // Interface for delete event click
    public interface OnEventDeleteClickListener {
        void onEventDeleteClick(int position);
    }

    // ViewHolder class for individual items
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle, eventLocation, eventDate, eventType, eventStartTime, eventDescription;
        ImageButton btnUpdate, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventType = itemView.findViewById(R.id.eventType);
            eventStartTime = itemView.findViewById(R.id.eventStartTime);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
