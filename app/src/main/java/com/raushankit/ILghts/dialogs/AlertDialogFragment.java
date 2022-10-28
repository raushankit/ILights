package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;

@SuppressWarnings("unused")
public class AlertDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "alert-dialog-fragment";

    private TextView titleText;
    private TextView bodyText;
    private MaterialButton positiveButton;
    private MaterialButton negativeButton;
    private WhichButtonClicked whichButtonClicked;

    private String actionType;
    @StringRes
    private int titleString;
    private String bodyString;
    private boolean enablePosButton;
    private boolean enableNegButton;
    @StringRes
    private int positiveButtonText;
    @StringRes
    private int negativeButtonText;

    public AlertDialogFragment() {

    }

    public static AlertDialogFragment newInstance(@StringRes int titleString, @NonNull Boolean enablePosButton, @NonNull Boolean enableNegButton) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("title_text", titleString);
        bundle.putBoolean("enable_positive_button", enablePosButton);
        bundle.putBoolean("enable_negative_button", enableNegButton);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_dialog, container, false);
        titleText = view.findViewById(R.id.alert_dialog_title);
        bodyText = view.findViewById(R.id.alert_dialog_body);
        positiveButton = view.findViewById(R.id.alert_dialog_positive_button);
        negativeButton = view.findViewById(R.id.alert_dialog_negative_button);
        Bundle args = getArguments();
        if (args != null) {
            titleString = args.getInt("title_text");
            enablePosButton = args.getBoolean("enable_positive_button");
            enableNegButton = args.getBoolean("enable_negative_button");
            positiveButton.setVisibility(enablePosButton ? View.VISIBLE : View.GONE);
            negativeButton.setVisibility(enableNegButton ? View.VISIBLE : View.GONE);
        }
        setText(titleText, titleString);
        if (!TextUtils.isEmpty(bodyString)) bodyText.setText(bodyString);
        setText(positiveButton, positiveButtonText);
        setText(negativeButton, negativeButtonText);
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }
        super.onStart();
    }

    public void addWhichButtonClickedListener(@NonNull WhichButtonClicked whichButtonClicked) {
        this.whichButtonClicked = whichButtonClicked;
    }

    public void setBodyString(@NonNull String bodyString) {
        this.bodyString = bodyString;
        if (bodyText == null) return;
        bodyText.setText(bodyString);
    }

    public void setTitleString(@StringRes int titleString) {
        this.titleString = titleString;
        if (titleText == null) return;
        setText(titleText, titleString);
    }

    public void setNegativeButtonText(@StringRes int negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        if (negativeButton == null) return;
        setText(negativeButton, negativeButtonText);
    }

    public void setPositiveButtonText(@StringRes int positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        if (positiveButton == null) return;
        setText(positiveButton, positiveButtonText);
    }

    public void setEnableNegButton(boolean enableNegButton) {
        this.enableNegButton = enableNegButton;
        if (negativeButton != null)
            negativeButton.setVisibility(enableNegButton ? View.VISIBLE : View.GONE);

    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setEnablePosButton(boolean enablePosButton) {
        this.enablePosButton = enablePosButton;
        if (positiveButton != null)
            positiveButton.setVisibility(enablePosButton ? View.VISIBLE : View.GONE);
    }

    private void setText(@NonNull TextView textView, @StringRes int stringRes) {
        try {
            textView.setText(stringRes);
        } catch (Resources.NotFoundException e) {
            Log.w(TAG, "setText: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        whichButtonClicked.onClick(view.getId() == R.id.alert_dialog_positive_button ? WhichButton.POSITIVE : WhichButton.NEGATIVE);
    }

    public enum WhichButton {
        POSITIVE, NEGATIVE
    }

    public interface WhichButtonClicked {
        void onClick(WhichButton whichButton);
    }
}
