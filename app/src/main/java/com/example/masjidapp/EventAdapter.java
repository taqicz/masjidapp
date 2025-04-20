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

        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(event.getDate());
        holder.eventType.setText(event.getType());
        String combinedTime = event.getStartTime() + " - " + event.getEndTime() + " WIB";
        holder.eventStartTime.setText(combinedTime);
        holder.eventDescription.setText(event.getDescription());

        if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(event.getImageUri()))
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_image);
        }

        holder.btnUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                updateClickListener.onEventUpdateClick(event, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setEvents(List<EventModel> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    public void setOnEventUpdateClickListener(OnEventUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    public interface OnEventUpdateClickListener {
        void onEventUpdateClick(EventModel event, int position);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle, eventLocation, eventDate, eventType, eventStartTime, eventDescription;
        ImageButton btnUpdate;

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
        }
    }
}
