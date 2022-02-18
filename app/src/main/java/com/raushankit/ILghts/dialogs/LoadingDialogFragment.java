package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.raushankit.ILghts.R;

public class LoadingDialogFragment extends DialogFragment {
    public static final String TAG = "loading-dialog-fragment";

    @StringRes
    private int title;
    @StringRes
    private int message;
    private TextView messageView;
    private TextView titleView;

    public LoadingDialogFragment(){}

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog, container, false);
        titleView = view.findViewById(R.id.loading_dialog_title);
        messageView = view.findViewById(R.id.loading_dialog_message);
        try {
            titleView.setText(title);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        try {
            messageView.setText(message);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setTitle(@StringRes int title) {
        this.title = title;
        if (titleView == null) return;
        try {
            titleView.setText(title);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(@StringRes int message) {
        this.message = message;
        if (messageView == null) return;
        try {
            messageView.setText(message);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }
}
