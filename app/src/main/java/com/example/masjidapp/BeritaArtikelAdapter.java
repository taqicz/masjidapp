package com.example.masjidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BeritaArtikelAdapter extends RecyclerView.Adapter<BeritaArtikelAdapter.BeritaArtikelViewHolder> {

    private List<BeritaArtikelModel> artikelList;

    private OnDeleteClickListener deleteClickListener;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;

    public BeritaArtikelAdapter(List<BeritaArtikelModel> artikelList) {
        this.artikelList = artikelList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public BeritaArtikelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_berita_artikel, parent, false);
        return new BeritaArtikelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaArtikelViewHolder holder, int position) {
        BeritaArtikelModel artikelItem = artikelList.get(position);
        holder.titleText.setText(artikelItem.getTitle());
        holder.contentText.setText(artikelItem.getContent());
        holder.tvCategory.setText(artikelItem.getKategori());

        // Klik tombol delete
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(position);
            }
        });

        // Klik biasa (buka detail)
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });

        // Klik lama (edit)
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    public void updateItem(int position, BeritaArtikelModel updatedModel) {
        artikelList.set(position, updatedModel);
        notifyItemChanged(position);
    }

    // ViewHolder
    public static class BeritaArtikelViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, contentText, tvCategory;
        ImageView btnDelete;

        public BeritaArtikelViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleArtikel);
            contentText = itemView.findViewById(R.id.contentArtikel);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Interface untuk hapus
    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    // Interface untuk klik biasa (buka detail)
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Interface untuk klik lama (edit)
    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }
}
