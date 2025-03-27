package com.example.masjidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MosqueAdapter extends RecyclerView.Adapter<MosqueAdapter.MosqueViewHolder> {

    private Context context;
    private List<MosqueModel> mosqueList;

    public MosqueAdapter(Context context, List<MosqueModel> mosqueList) {
        this.context = context;
        this.mosqueList = mosqueList;
    }

    @NonNull
    @Override
    public MosqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mosque, parent, false);
        return new MosqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MosqueViewHolder holder, int position) {
        MosqueModel mosque = mosqueList.get(position);

        holder.mosqueName.setText(mosque.getName());
        holder.mosqueAddress.setText(mosque.getAddress());
        holder.mosqueRating.setRating(mosque.getRating());
        holder.ratingText.setText(mosque.getRating() + " (120)");
        holder.mosqueDistance.setText(mosque.getDistance());

        // Set click listener for favorite button
        holder.favoriteButton.setOnClickListener(v -> {
            Toast.makeText(context, "Ditambahkan ke favorit: " + mosque.getName(), Toast.LENGTH_SHORT).show();
        });

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Masjid dipilih: " + mosque.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return mosqueList.size();
    }

    public static class MosqueViewHolder extends RecyclerView.ViewHolder {
        ImageView mosqueThumbnail;
        TextView mosqueName, mosqueAddress, ratingText, mosqueDistance;
        RatingBar mosqueRating;
        ImageButton favoriteButton;

        public MosqueViewHolder(@NonNull View itemView) {
            super(itemView);
            mosqueThumbnail = itemView.findViewById(R.id.mosqueThumbnail);
            mosqueName = itemView.findViewById(R.id.mosqueName);
            mosqueAddress = itemView.findViewById(R.id.mosqueAddress);
            mosqueRating = itemView.findViewById(R.id.mosqueRating);
            ratingText = itemView.findViewById(R.id.ratingText);
            mosqueDistance = itemView.findViewById(R.id.mosqueDistance);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}

