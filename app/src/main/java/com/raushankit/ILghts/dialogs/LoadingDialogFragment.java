package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.raushankit.ILghts.R;

public class LoadingDialogFragment extends DialogFragment {
    public static final String TAG = "loading-dialog-fragment";

    private String title;

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog,container,false);
        TextView titleText = view.findViewById(R.id.loading_dialog_title);
        if(title != null) { titleText.setText(title); }
        return view;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onStart() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }
}
