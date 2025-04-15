package com.example.masjidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonasiAdapter extends RecyclerView.Adapter<DonasiAdapter.ViewHolder> {

    private List<ModelDonasi> list;

    public DonasiAdapter(List<ModelDonasi> list){
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelDonasi item = list.get(position);
        holder.tvNama.setText(item.getNama());
        holder.tvJumlah.setText(item.getJumlah());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvJumlah;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
        }
    }
}
