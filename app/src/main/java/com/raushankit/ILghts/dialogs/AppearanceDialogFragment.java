package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.ColorSelectorAdapter;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.Arrays;
import java.util.stream.Collectors;

import kotlin.Triple;

public class AppearanceDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "AppearanceDialogFragment";

    private Triple<Integer, Integer, String> selectedData;
    private CallBack<Pair<Integer, String>> callBack;

    public static final String COLORS = "colors";
    public static final String COLOR_NAMES = "color_names";
    public static final String CHECKED_INDEX = "checked_index";

    private int checkedIndex;

    public static AppearanceDialogFragment newInstance(int[] colors, String[] colorNames, int checkedIndex) {
        Bundle args = new Bundle();
        AppearanceDialogFragment fragment = new AppearanceDialogFragment();
        args.putIntArray(COLORS, colors);
        args.putStringArray(COLOR_NAMES, colorNames);
        args.putInt(CHECKED_INDEX, checkedIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_settings_aapearance, container, false);
        MaterialCardView cardView = view.findViewById(R.id.frag_dialog_settings_appearance_card);
        LinearLayout layout = view.findViewById(R.id.frag_dialog_settings_appearance_parent);
        RecyclerView recyclerView = view.findViewById(R.id.frag_dialog_settings_appearance_list);
        MaterialButton cancel = view.findViewById(R.id.frag_dialog_settings_appearance_negative_button);
        MaterialButton done = view.findViewById(R.id.frag_dialog_settings_appearance_positive_button);
        Bundle args = getArguments();
        assert args != null;
        int[] colors = args.getIntArray(COLORS);
        String[] colorNames = args.getStringArray(COLOR_NAMES);
        checkedIndex = args.getInt(CHECKED_INDEX);
        ColorSelectorAdapter adapter;
        boolean isNight = Color.BLACK == MaterialColors.getColor(inflater.getContext(), R.attr.colorOnPrimary, Color.BLACK);
        int colorPrimary = inflater.getContext().getColor(colors[checkedIndex]);
        int defColor = isNight? colorPrimary: inflater.getContext().getColor(R.color.charcoal);
        layout.setBackgroundColor(defColor);
        cancel.setTextColor(colorPrimary);
        done.setBackgroundColor(colorPrimary);
        cardView.setStrokeColor(colorPrimary);
        selectedData = new Triple<>(checkedIndex, colors[checkedIndex], colorNames[checkedIndex]);
        adapter = new ColorSelectorAdapter(Arrays.stream(colors).boxed().collect(Collectors.toList())
                , Arrays.stream(colorNames).collect(Collectors.toList()),
                checkedIndex, dt -> {
            int color = dt.getSecond();
            cancel.setTextColor(color);
            done.setBackgroundColor(color);
            cardView.setStrokeColor(color);
            if(isNight) {
                layout.setBackgroundColor(color);
            }
            selectedData = dt;
        });
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void addCallback(@NonNull CallBack<Pair<Integer, String>> callBack) {
        this.callBack = callBack;
    }

    public void setCheckedIndex(int checkedIndex) {
        this.checkedIndex = checkedIndex;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.frag_dialog_settings_appearance_positive_button) {
            callBack.onClick(Pair.create(selectedData.component1(), selectedData.component3()));
        }
        dismiss();
    }
}
