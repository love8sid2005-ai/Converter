package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Security;

import java.util.List;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.SecurityViewHolder> {

    private List<Security> securities;
    private OnSecurityClickListener listener;

    public interface OnSecurityClickListener {
        void onSecurityClick(Security security);
    }

    public SecurityAdapter(List<Security> securities, OnSecurityClickListener listener) {
        this.securities = securities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SecurityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_security, parent, false);
        return new SecurityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityViewHolder holder, int position) {
        Security security = securities.get(position);

        // Устанавливаем иконку с единым оранжевым цветом
        holder.ivIcon.setImageResource(security.getIconResId());
        holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));

        holder.tvSymbol.setText(security.getSymbol());
        holder.tvName.setText(security.getName());
        holder.tvPrice.setText(security.getFormattedPrice());
        holder.tvChange.setText(security.getFormattedChange());
        holder.tvVolume.setText("Объем: " + security.getFormattedVolume());
        holder.tvSector.setText(security.getSector());

        // Отображаем доступное количество
        if (security.getAvailableQuantity() > 0) {
            String availableText;
            int available = security.getAvailableQuantity();
            if (available >= 1000) {
                availableText = String.format("%.1fK", available / 1000.0);
            } else {
                availableText = String.valueOf(available);
            }
            holder.tvAvailable.setText("Доступно: " + availableText);
            holder.tvAvailable.setVisibility(View.VISIBLE);
        } else {
            holder.tvAvailable.setVisibility(View.GONE);
        }

        // Отображаем количество в портфеле
        int ownedQuantity = security.getOwnedQuantity();
        if (ownedQuantity > 0) {
            String ownedText;
            if (ownedQuantity >= 1000) {
                ownedText = String.format("%.1fK", ownedQuantity / 1000.0);
            } else {
                ownedText = String.valueOf(ownedQuantity);
            }
            holder.tvOwned.setText("В портфеле: " + ownedText);
            holder.tvOwned.setVisibility(View.VISIBLE);
        } else {
            holder.tvOwned.setVisibility(View.GONE);
        }

        int color = ContextCompat.getColor(holder.itemView.getContext(),
                security.isRising() ? R.color.success : R.color.error);
        holder.tvChange.setTextColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSecurityClick(security);
            }
        });
    }

    @Override
    public int getItemCount() {
        return securities.size();
    }

    public void updateSecurityOwnership(Security updatedSecurity) {
        for (int i = 0; i < securities.size(); i++) {
            if (securities.get(i).getSymbol().equals(updatedSecurity.getSymbol())) {
                securities.set(i, updatedSecurity);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class SecurityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvSymbol, tvName, tvPrice, tvChange, tvVolume, tvSector, tvAvailable, tvOwned;

        SecurityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivSecurityIcon);
            tvSymbol = itemView.findViewById(R.id.tvSecuritySymbol);
            tvName = itemView.findViewById(R.id.tvSecurityName);
            tvPrice = itemView.findViewById(R.id.tvSecurityPrice);
            tvChange = itemView.findViewById(R.id.tvSecurityChange);
            tvVolume = itemView.findViewById(R.id.tvSecurityVolume);
            tvSector = itemView.findViewById(R.id.tvSecuritySector);
            tvAvailable = itemView.findViewById(R.id.tvSecurityAvailable);
            tvOwned = itemView.findViewById(R.id.tvSecurityOwned);
        }
    }
}