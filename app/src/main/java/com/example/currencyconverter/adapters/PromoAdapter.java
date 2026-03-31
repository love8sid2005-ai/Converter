package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Promo;

import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder> {

    private List<Promo> promoList;

    public PromoAdapter(List<Promo> promoList) {
        this.promoList = promoList;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Promo promo = promoList.get(position);

        holder.ivImage.setImageResource(promo.getImageResId());
        holder.ivImage.setColorFilter(holder.itemView.getContext().getColor(R.color.accent));

        holder.tvTitle.setText(promo.getTitle());
        holder.tvDescription.setText(promo.getDescription());
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription;

        PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivPromoImage);
            tvTitle = itemView.findViewById(R.id.tvPromoTitle);
            tvDescription = itemView.findViewById(R.id.tvPromoDescription);
        }
    }
}