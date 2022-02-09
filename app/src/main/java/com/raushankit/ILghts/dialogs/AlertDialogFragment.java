package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private String titleString;
    private String bodyString;
    private boolean enablePosButton;
    private boolean enableNegButton;
    private String positiveButtonText;
    private String negativeButtonText;

    public AlertDialogFragment() {

    }

    public static AlertDialogFragment newInstance(@NonNull String titleString, @NonNull Boolean enablePosButton, @NonNull Boolean enableNegButton) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title_text", titleString);
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
        if(args != null){
            titleString = args.getString("title_text");
            enablePosButton = args.getBoolean("enable_positive_button");
            enableNegButton = args.getBoolean("enable_negative_button");
            positiveButton.setVisibility(enablePosButton?View.VISIBLE:View.GONE);
            negativeButton.setVisibility(enableNegButton?View.VISIBLE:View.GONE);
        }
        if(!TextUtils.isEmpty(titleString)){ titleText.setText(titleString); }
        if(!TextUtils.isEmpty(bodyString)){ bodyText.setText(bodyString); }
        if(!TextUtils.isEmpty(positiveButtonText)){ positiveButton.setText(positiveButtonText); }
        if(!TextUtils.isEmpty(negativeButtonText)){ negativeButton.setText(negativeButtonText); }
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }

    public void addWhichButtonClickedListener(@NonNull WhichButtonClicked whichButtonClicked){
        this.whichButtonClicked = whichButtonClicked;
    }

    public void setBodyString(@NonNull String bodyString) {
        this.bodyString = bodyString;
        if(bodyText != null) bodyText.setText(bodyString);
    }

    public void setTitleString(@NonNull String titleString) {
        this.titleString = titleString;
        if(titleText != null) titleText.setText(titleString);
    }

    public void setNegativeButtonText(@NonNull String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        if(negativeButton != null) negativeButton.setText(negativeButtonText);
    }

    public void setPositiveButtonText(@NonNull String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        if(positiveButton != null) positiveButton.setText(positiveButtonText);
    }

    public void setEnableNegButton(boolean enableNegButton) {
        this.enableNegButton = enableNegButton;
        if(negativeButton != null) negativeButton.setVisibility(enableNegButton?View.VISIBLE:View.GONE);

    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setEnablePosButton(boolean enablePosButton) {
        this.enablePosButton = enablePosButton;
        if(positiveButton != null) positiveButton.setVisibility(enablePosButton?View.VISIBLE:View.GONE);
    }

    @Override
    public void onClick(View view) {
        whichButtonClicked.onClick(view.getId()==R.id.alert_dialog_positive_button?WhichButton.POSITIVE:WhichButton.NEGATIVE);
    }

    public interface WhichButtonClicked {
        void onClick(WhichButton whichButton);
    }

    public enum WhichButton {
        POSITIVE, NEGATIVE
    }
}
