package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;

public class ConsentDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "ConsentDialogFragment";
    private ActionClickListener listener;

    public ConsentDialogFragment() {

    }

    public static ConsentDialogFragment newInstance() {
        return new ConsentDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dialog_analytics_consent, container, false);
        MaterialButton privacyBtn = view.findViewById(R.id.consent_dialog_privacy_policy_button);
        MaterialButton negativeBtn = view.findViewById(R.id.consent_dialog_negative_button);
        MaterialButton positiveBtn = view.findViewById(R.id.consent_dialog_positive_button);
        privacyBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        super.onStart();
    }

    public void addOnActionClickListener(@NonNull ActionClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener == null ){
            dismiss();
            return;
        }
        int id = view.getId();
        if(id == R.id.consent_dialog_privacy_policy_button){
            listener.onClick(Action.POLICY);
        }else if(id == R.id.consent_dialog_negative_button){
            listener.onClick(Action.DISAGREE);
        }else if(id == R.id.consent_dialog_positive_button){
            listener.onClick(Action.AGREE);
        }else{
            Log.w(TAG, "onClick: unknown click event");
        }
    }

    public interface ActionClickListener {
        void onClick(ConsentDialogFragment.Action action);
    }

    public enum Action {
        AGREE, DISAGREE, POLICY
    }
}
