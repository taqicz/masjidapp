package com.example.masjidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BeritaTrendingAdapter extends RecyclerView.Adapter<BeritaTrendingAdapter.BeritaTrendingViewHolder> {

    private List<BeritaTrendingModel> trendingList;

    // Constructor
    public BeritaTrendingAdapter(List<BeritaTrendingModel> trendingList) {
        this.trendingList = trendingList;
    }

    @NonNull
    @Override
    public BeritaTrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita_trending, parent, false);
        return new BeritaTrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaTrendingViewHolder holder, int position) {
        BeritaTrendingModel trendingItem = trendingList.get(position);
        holder.categoryText.setText(trendingItem.getCategory());
        holder.titleText.setText(trendingItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    // ViewHolder class
    public static class BeritaTrendingViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, titleText;
        public BeritaTrendingViewHolder(View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryTrending);
            titleText    = itemView.findViewById(R.id.titleTrending);
        }
    }

}
