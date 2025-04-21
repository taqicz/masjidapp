package com.example.masjidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.BukuViewHolder> {

    private Context context;
    private ArrayList<BukuModel> listBuku;

    public BukuAdapter(Context context, ArrayList<BukuModel> listBuku) {
        this.context = context;
        this.listBuku = listBuku;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(BukuModel buku);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public BukuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buku, parent, false);
        return new BukuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BukuViewHolder holder, int position) {
        BukuModel buku = listBuku.get(position);
        holder.imgBuku.setImageResource(buku.getGambar());
        holder.txtDeskripsi.setText(buku.getDeskripsi());
        holder.txtTahun.setText(buku.getTahun());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(buku);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listBuku.size();
    }

    public static class BukuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBuku;
        TextView txtDeskripsi, txtTahun;

        public BukuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBuku = itemView.findViewById(R.id.imgBuku);
            txtDeskripsi = itemView.findViewById(R.id.txtDeskripsi);
            txtTahun = itemView.findViewById(R.id.txtTahun);
        }
    }
}
