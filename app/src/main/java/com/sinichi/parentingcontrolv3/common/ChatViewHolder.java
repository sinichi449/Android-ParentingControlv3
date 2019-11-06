package com.sinichi.parentingcontrolv3.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sinichi.parentingcontrolv3.R;

public class ChatViewHolder {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView tvTime;
//        private TextView messengerTextView; // Username
        ConstraintLayout constraintLayout;
        ConstraintLayout constraintParent;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_message_chat);
            messageImageView = itemView.findViewById(R.id.img_message_chat);
            constraintLayout = itemView.findViewById(R.id.constraint_item_message);
            constraintParent = itemView.findViewById(R.id.constraint_parent);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
