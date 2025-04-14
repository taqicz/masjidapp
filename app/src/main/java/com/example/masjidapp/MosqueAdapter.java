package com.example.masjidapp;

import android.content.Context;
import android.content.Intent;
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
    private OnItemClickListener onItemClickListener;

    public MosqueAdapter(Context context, List<MosqueModel> mosqueList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mosqueList = mosqueList;
        this.onItemClickListener = onItemClickListener;
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

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            // Mengirimkan data ke Activity Profil Masjid
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(mosque);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mosqueList.size();
    }

    // Define interface for item click
    public interface OnItemClickListener {
        void onItemClick(MosqueModel mosque);
    }

    public static class MosqueViewHolder extends RecyclerView.ViewHolder {
        ImageView mosqueThumbnail;
        TextView mosqueName, mosqueAddress, ratingText, mosqueDistance;
        RatingBar mosqueRating;

        public MosqueViewHolder(@NonNull View itemView) {
            super(itemView);
            mosqueThumbnail = itemView.findViewById(R.id.mosqueThumbnail);
            mosqueName = itemView.findViewById(R.id.mosqueName);
            mosqueAddress = itemView.findViewById(R.id.mosqueAddress);
            mosqueRating = itemView.findViewById(R.id.mosqueRating);
            ratingText = itemView.findViewById(R.id.ratingText);
            mosqueDistance = itemView.findViewById(R.id.mosqueDistance);
        }
    }
}


