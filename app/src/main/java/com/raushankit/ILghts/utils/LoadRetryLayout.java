package com.raushankit.ILghts.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.raushankit.ILghts.R;

@SuppressWarnings("unused")
public class LoadRetryLayout {

    private final LinearLayout parent;

    private final MaterialTextView message;

    private final ProgressBar progressBar;

    private final MaterialButton button;

    public boolean endOfPage;

    public LoadRetryLayout(@NonNull LinearLayout linearLayout) {
        parent = linearLayout;
        endOfPage = false;
        message = linearLayout.findViewById(R.id.loading_details_header_footer_error_msg);
        progressBar = linearLayout.findViewById(R.id.loading_details_header_footer_progress_bar);
        button = linearLayout.findViewById(R.id.loading_details_header_footer_retry_button);
    }

    public void visibility(boolean visible) {
        parent.setVisibility(!endOfPage && visible? View.VISIBLE: View.GONE);
    }

    public void lastItem() {
        parent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
        button.setText(R.string.load_more_boards);
        message.setVisibility(View.VISIBLE);
    }

    public void loading() {
        parent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
    }

    public void error() {
        parent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
        button.setText(R.string.retry);
        message.setVisibility(View.VISIBLE);
    }

    public void setEndOfPage(boolean endOfPage) {
        this.endOfPage = endOfPage;
        if(endOfPage) {
            parent.setVisibility(View.GONE);
        }
    }
}
