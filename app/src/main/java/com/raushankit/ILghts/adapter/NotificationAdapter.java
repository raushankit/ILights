package com.raushankit.ILghts.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.utils.StringUtils;

public class NotificationAdapter extends PagingDataAdapter<Notification, NotificationAdapter.NotificationViewHolder> {

    public NotificationAdapter() {
        super(Notification.DIFF_UTIL);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_notification_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView body;
        private final TextView time;
        private final MaterialButton accept;
        private final MaterialButton reject;
        private final ImageView imageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.board_notification_list_item_icon);
            body = itemView.findViewById(R.id.board_notification_list_item_title);
            time = itemView.findViewById(R.id.board_notification_list_item_time);
            accept = itemView.findViewById(R.id.board_notification_list_item_accept_button);
            reject = itemView.findViewById(R.id.board_notification_list_item_reject_button);
        }

        public void bind(@Nullable Notification item){
            if(item == null) { return; }
            imageView.setImageResource(TextUtils.equals(NotificationType.TEXT, item.getType())? R.drawable.ic_baseline_notifications_24: R.drawable.ic_edit_notifications_filled);
            body.setText(item.getBody());
            time.setText(StringUtils.formattedTime(-1*item.getTime()));
            accept.setVisibility(TextUtils.equals(item.getType(), NotificationType.TEXT)? View.GONE: View.VISIBLE);
            reject.setVisibility(TextUtils.equals(item.getType(), NotificationType.TEXT)? View.GONE: View.VISIBLE);
        }
    }
}
