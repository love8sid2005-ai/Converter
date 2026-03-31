package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Card> cards;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(Card card);
    }

    public CardAdapter(List<Card> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);

        holder.ivCardIcon.setImageResource(card.getIconResId());
        holder.tvCardType.setText(card.getType());
        holder.tvCardNumber.setText(card.getNumber());
        holder.tvCardStatus.setText("Активна");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCardIcon;
        TextView tvCardType, tvCardNumber, tvCardStatus;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCardIcon = itemView.findViewById(R.id.ivCardIcon);
            tvCardType = itemView.findViewById(R.id.tvCardType);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            tvCardStatus = itemView.findViewById(R.id.tvCardStatus);
        }
    }
}