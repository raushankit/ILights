package com.raushankit.ILghts.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.utils.StringUtils;

public class NotificationAdapter extends PagingDataAdapter<Notification, NotificationAdapter.NotificationViewHolder> {
    private static final String TAG = "NotificationAdapter";
    private final WhichButtonClickedListener listener;
    @ColorInt private final int unSeenColor;
    @ColorInt private int seenColor;

    public NotificationAdapter(@ColorInt final int unSeenColor, @NonNull final WhichButtonClickedListener listener) {
        super(Notification.DIFF_UTIL);
        this.listener = listener;
        this.unSeenColor = unSeenColor;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        seenColor = parent.getContext()
                .getColor(R.color.cards_and_dialogs_color);
        return new NotificationViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_notification_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView cardView;
        private final TextView body;
        private final TextView time;
        private final MaterialButton accept;
        private final MaterialButton reject;
        private final ImageView imageView;
        private final LinearLayout loadingLayout;
        private final ProgressBar progressBar;
        private final MaterialTextView loadingTextView;
        private final MaterialButton retryButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.board_notification_list_card);
            imageView = itemView.findViewById(R.id.board_notification_list_item_icon);
            body = itemView.findViewById(R.id.board_notification_list_item_title);
            time = itemView.findViewById(R.id.board_notification_list_item_time);
            accept = itemView.findViewById(R.id.board_notification_list_item_accept_button);
            reject = itemView.findViewById(R.id.board_notification_list_item_reject_button);
            loadingLayout = itemView.findViewById(R.id.board_notification_list_item_loader_layout);
            progressBar = loadingLayout.findViewById(R.id.loading_details_header_footer_progress_bar);
            loadingTextView = loadingLayout.findViewById(R.id.loading_details_header_footer_error_msg);
            retryButton = loadingLayout.findViewById(R.id.loading_details_header_footer_retry_button);
            cardView.setOnClickListener(this);
            accept.setOnClickListener(this);
            reject.setOnClickListener(this);
            retryButton.setOnClickListener(this);
        }

        public void bind(@Nullable Notification item){
            if(item == null) { return; }
            imageView.setImageResource(TextUtils.equals(NotificationType.TEXT, item.getType())? R.drawable.ic_baseline_notifications_24: R.drawable.ic_edit_notifications_filled);
            body.setText(item.getBody());
            cardView.setBackgroundColor(item.isSeen()? seenColor: unSeenColor);
            if(item.getPagination() > NotificationType.END_OF_PAGE){
                loadingTextView.setVisibility(item.getPagination() == NotificationType.LOADING? View.GONE: View.VISIBLE);
                progressBar.setVisibility(item.getPagination() == NotificationType.LOADING? View.VISIBLE: View.GONE);
                retryButton.setVisibility(item.getPagination() == NotificationType.LOADING? View.GONE: View.VISIBLE);
                loadingTextView.setText(R.string.load_older_notifications);
                retryButton.setText(R.string.load);
                loadingLayout.setVisibility(View.VISIBLE);
            }else{
                loadingLayout.setVisibility(View.GONE);
            }
            Log.e(TAG, "bind: item = " + item);
            time.setText(StringUtils.formattedTime(-1*item.getTime()));
            accept.setVisibility(TextUtils.equals(item.getType(), NotificationType.TEXT)? View.GONE: View.VISIBLE);
            reject.setVisibility(TextUtils.equals(item.getType(), NotificationType.TEXT)? View.GONE: View.VISIBLE);
            accept.setEnabled(!TextUtils.equals(item.getType(), NotificationType.ACTION_DONE));
            reject.setEnabled(!TextUtils.equals(item.getType(), NotificationType.ACTION_DONE));
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.board_notification_list_item_accept_button){
                listener.onClick(WhichButton.ACTION_ACCEPT, getItem(getBindingAdapterPosition()));
            }else if(id == R.id.board_notification_list_item_reject_button){
                listener.onClick(WhichButton.ACTION_REJECT, getItem(getBindingAdapterPosition()));
            }else if(id == R.id.loading_details_header_footer_retry_button){
                listener.onClick(WhichButton.LOAD_MORE, getItem(getBindingAdapterPosition()));
            }else if(id == R.id.board_notification_list_card){
                listener.onClick(WhichButton.SEEN_BUTTON, getItem(getBindingAdapterPosition()));
            }else{
                Log.i(TAG, "onClick: unknown click event");
            }
        }
    }

    public enum WhichButton {
        ACTION_ACCEPT, ACTION_REJECT, LOAD_MORE, SEEN_BUTTON
    }

    public interface WhichButtonClickedListener {
        void onClick(WhichButton type, Notification data);
    }
}
