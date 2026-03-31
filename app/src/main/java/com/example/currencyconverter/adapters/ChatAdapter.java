package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_AI = 1;

    private List<ChatMessage> messages;
    private SimpleDateFormat timeFormat;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? TYPE_USER : TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_ai, parent, false);
            return new AIMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        String time = timeFormat.format(new Date(message.getTimestamp()));

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).messageText.setText(message.getMessage());
            ((UserMessageViewHolder) holder).timeText.setText(time);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).messageText.setText(message.getMessage());
            ((AIMessageViewHolder) holder).timeText.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }

    static class AIMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        AIMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}