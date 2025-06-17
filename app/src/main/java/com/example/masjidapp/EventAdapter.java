/**
 * EventAdapter adalah adapter RecyclerView yang digunakan untuk menampilkan daftar event dalam bentuk list/card.
 *
 * Fitur:
 * - Menampilkan data event: gambar, judul, lokasi, tanggal, jam, jenis, deskripsi.
 * - Navigasi ke EventDetailActivity saat item diklik.
 * - Mendukung aksi update dan delete melalui tombol di setiap item.
 */

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

    // Listener untuk aksi update dan delete
    private OnEventUpdateClickListener updateClickListener;
    private OnEventDeleteClickListener deleteClickListener;

    // Konstruktor adapter
    public EventAdapter(Context context, List<EventModel> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    // Membuat tampilan item
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    // Mengikat data ke tampilan item
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);

        // Isi data ke elemen-elemen tampilan
        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(event.getDate());
        holder.eventType.setText(event.getType());

        String combinedTime = event.getStartTime() + " - " + event.getEndTime() + " WIB";
        holder.eventStartTime.setText(combinedTime);
        holder.eventDescription.setText(event.getDescription());

        // Menampilkan gambar menggunakan Glide
        if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(event.getImageUri()))
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_image); // Gambar default jika tidak ada URL
        }

        // Saat item diklik, buka EventDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("EVENT_DETAIL", event); // Kirim objek event
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });

        // Aksi saat tombol update ditekan
        holder.btnUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                updateClickListener.onEventUpdateClick(event, position); // Panggil listener update
            }
        });

        // Aksi saat tombol delete ditekan
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onEventDeleteClick(position); // Panggil listener delete
            }
        });
    }

    // Mengembalikan jumlah item dalam list
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Mengganti seluruh list event dan perbarui tampilan
    public void setEvents(List<EventModel> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged(); // Beri tahu adapter bahwa datanya berubah
    }

    // Menambahkan satu event ke list
    public void addEvent(EventModel event) {
        eventList.add(event);
        notifyItemInserted(eventList.size() - 1);
    }

    // Menghapus satu event dari list berdasarkan posisi
    public void removeEvent(int position) {
        eventList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventList.size());
    }

    // Set listener untuk update
    public void setOnEventUpdateClickListener(OnEventUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    // Set listener untuk delete
    public void setOnEventDeleteClickListener(OnEventDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // Interface callback untuk aksi update
    public interface OnEventUpdateClickListener {
        void onEventUpdateClick(EventModel event, int position);
    }

    // Interface callback untuk aksi delete
    public interface OnEventDeleteClickListener {
        void onEventDeleteClick(int position);
    }

    // ViewHolder untuk item event
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
