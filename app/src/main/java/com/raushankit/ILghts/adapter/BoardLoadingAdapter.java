package com.raushankit.ILghts.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;

public class BoardLoadingAdapter extends LoadStateAdapter<BoardLoadingAdapter.BoardLoadingViewHolder> {
    private static final String TAG = "BoardLoadingAdapter";
    private final RetryCallback retryCallback;

    public BoardLoadingAdapter(@NonNull RetryCallback retryCallback) {
        this.retryCallback = retryCallback;
    }

    @NonNull
    @Override
    public BoardLoadingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull LoadState loadState) {
        return new BoardLoadingViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.loading_details_header_footer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardLoadingViewHolder boardLoadingViewHolder, @NonNull LoadState loadState) {
        boardLoadingViewHolder.bind(loadState);
    }

    class BoardLoadingViewHolder extends RecyclerView.ViewHolder {
        private final TextView errorText;
        private final ProgressBar progressBar;
        private final MaterialButton button;

        public BoardLoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            errorText = itemView.findViewById(R.id.loading_details_header_footer_error_msg);
            progressBar = itemView.findViewById(R.id.loading_details_header_footer_progress_bar);
            button = itemView.findViewById(R.id.loading_details_header_footer_retry_button);
            button.setOnClickListener(view -> retryCallback.retry());
        }

        void bind(LoadState loadState){
            if (loadState instanceof LoadState.Error) {
                LoadState.Error loadStateError = (LoadState.Error) loadState;
                errorText.setText(loadStateError.getError().getLocalizedMessage());
            }
            progressBar.setVisibility(loadState instanceof LoadState.Loading
                    ? View.VISIBLE : View.GONE);
            button.setVisibility(loadState instanceof LoadState.Error
                    ? View.VISIBLE : View.GONE);
            errorText.setVisibility(loadState instanceof LoadState.Error
                    ? View.VISIBLE : View.GONE);
        }
    }

    public interface RetryCallback {
        void retry();
    }
}
