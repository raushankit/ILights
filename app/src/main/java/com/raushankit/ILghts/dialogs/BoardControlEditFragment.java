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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.PinItemSelectorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardControlEditFragment extends DialogFragment {

    public static final String TAG = "BoardControlEditFragment";

    public static final String PINS_ALLOWED = "pins_allowed";

    public BoardControlEditFragment() {

    }

    public static BoardControlEditFragment newInstance(int pinsAllowed) {
        Bundle args = new Bundle();
        BoardControlEditFragment fragment = new BoardControlEditFragment();
        args.putInt(PINS_ALLOWED, pinsAllowed);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_board_control_edit, container, false);
        TextView titleText = view.findViewById(R.id.fragment_dialog_board_control_edit_title);
        RecyclerView layout = view.findViewById(R.id.fragment_dialog_board_control_edit_flex_layout);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(inflater.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        layout.setLayoutManager(layoutManager);
        List<Integer> list = new ArrayList<>();
        for(int i = 1;i <= 30;++i) {
            list.add(i);
        }
        PinItemSelectorAdapter adapter = new PinItemSelectorAdapter(list);
        layout.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }
        super.onStart();
    }

}
