package com.example.masjidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BeritaArtikelAdapter extends RecyclerView.Adapter<BeritaArtikelAdapter.BeritaArtikelViewHolder> {

    private Context context;
    private List<BeritaArtikelModel> artikelList;

    private OnDeleteClickListener deleteClickListener;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener longClickListener;

    public BeritaArtikelAdapter(Context context, List<BeritaArtikelModel> artikelList) {
        this.context = context;
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

        if (holder.tvUsername != null) {
            if (artikelItem.getAuthorName() != null && !artikelItem.getAuthorName().isEmpty()) {
                holder.tvUsername.setText(artikelItem.getAuthorName());
            } else {
                holder.tvUsername.setText("Anonim");
            }
        }

        if (holder.imageArtikel != null && context != null) {
            Glide.with(context)
                    .load(artikelItem.getImageUrl())
                    .placeholder(R.drawable.logo_app)
                    .into(holder.imageArtikel);
        }

        if (holder.tvTimePosted != null) {
            long timeInMillis = artikelItem.getTimestamp();

            if (timeInMillis > 0) {
                PrettyTime p = new PrettyTime(new Locale("in","ID"));
                String timeAgo = p.format(new Date(timeInMillis));

                holder.tvTimePosted.setText(timeAgo);
            } else {
                holder.tvTimePosted.setText(""); // Kosongkan jika tidak ada timestamp
            }
        }

        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDelete(position);
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(artikelItem);
            }
        });

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

    public static class BeritaArtikelViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, contentText, tvCategory, tvUsername; // 1. Tambahkan TextView untuk Username
        ImageView btnDelete;
        ImageView imageArtikel;
        TextView tvTimePosted;

        public BeritaArtikelViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleArtikel);
            contentText = itemView.findViewById(R.id.contentArtikel);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imageArtikel = itemView.findViewById(R.id.imageArtikel);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimePosted = itemView.findViewById(R.id.tvTimePosted);
        }
    }

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(BeritaArtikelModel artikel);
    }
    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }
}