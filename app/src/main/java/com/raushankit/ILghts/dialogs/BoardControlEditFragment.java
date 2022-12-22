package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.PinItemSelectorAdapter;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.PinEditDataViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardControlEditFragment extends DialogFragment implements View.OnClickListener, CallBack<Integer> {

    public static final String TAG = "BoardControlEditFragment";

    public static final String PINS_ALLOWED = "pins_allowed";

    public static final String PIN = "pin";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    private TextInputLayout titleLayout;
    private TextInputEditText titleEditText;
    private final TextWatcher titleWatcher;

    private TextInputLayout descLayout;
    private TextInputEditText descEditText;
    private final TextWatcher descWatcher;

    private TextView pinSelectionErrorMessageText;

    private PinEditDataViewModel viewModel;

    private int selectedPin = -1;

    public BoardControlEditFragment() {
        titleWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        descWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static BoardControlEditFragment newInstance(List<Integer> pinsAllowed, Integer pin, String name, String description) {
        Bundle args = new Bundle();
        BoardControlEditFragment fragment = new BoardControlEditFragment();
        args.putIntegerArrayList(PINS_ALLOWED, new ArrayList<>(pinsAllowed));
        if(!TextUtils.isEmpty(name)) {
            args.putString(TITLE, name);
        }
        if(!TextUtils.isEmpty(description)) {
            args.putString(DESCRIPTION, description);
        }
        if(pin != null) {
            args.putInt(PIN, pin);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PinEditDataViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_board_control_edit, container, false);
        TextView titleText = view.findViewById(R.id.fragment_dialog_board_control_edit_title);
        TextView listTitle = view.findViewById(R.id.fragment_dialog_board_control_edit_flex_layout_title);
        LinearLayout layout = view.findViewById(R.id.fragment_dialog_board_control_edit_flex_layout_parent);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_dialog_board_control_edit_flex_layout);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(inflater.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        recyclerView.setLayoutManager(layoutManager);
        titleLayout = view.findViewById(R.id.fragment_dialog_board_control_edit_name_input_layout);
        titleEditText = view.findViewById(R.id.fragment_dialog_board_control_edit_name_input_edit_text);
        descLayout = view.findViewById(R.id.fragment_dialog_board_control_edit_description_input_layout);
        descEditText = view.findViewById(R.id.fragment_dialog_board_control_edit_description_input_edit_text);
        pinSelectionErrorMessageText = view.findViewById(R.id.fragment_dialog_board_control_edit_error_subtitle);
        MaterialButton cancel = view.findViewById(R.id.fragment_dialog_board_control_edit_cancel_button);
        MaterialButton done = view.findViewById(R.id.fragment_dialog_board_control_edit_ok_button);
        Bundle args = getArguments();
        assert args != null;
        if(args.containsKey(PIN)) {
            selectedPin = args.getInt(PIN);
        }
        if(args.containsKey(TITLE)) {
            titleEditText.setText(args.getString(TITLE));
        }
        if(args.containsKey(DESCRIPTION)) {
            descEditText.setText(args.getString(DESCRIPTION));
        }
        PinItemSelectorAdapter adapter;
        List<Integer> pins = args.getIntegerArrayList(PINS_ALLOWED);
        if(CollectionUtils.isEmpty(pins)) {
            titleText.setText(R.string.edit_pin);
            listTitle.setVisibility(View.GONE);
            adapter = new PinItemSelectorAdapter(Collections.singletonList(selectedPin), 0, this);
        } else {
            listTitle.setVisibility(View.VISIBLE);
            titleText.setText(R.string.add_pin);
            adapter = new PinItemSelectorAdapter(pins, -1, this);
        }
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        titleEditText.addTextChangedListener(titleWatcher);
        descEditText.addTextChangedListener(descWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        titleEditText.removeTextChangedListener(titleWatcher);
        descEditText.removeTextChangedListener(descWatcher);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.fragment_dialog_board_control_edit_cancel_button) {
            dismiss();
        }
        else  if (id == R.id.fragment_dialog_board_control_edit_ok_button) {
            boolean flag = true;
            if(TextUtils.isEmpty(titleEditText.getText())) {
                titleLayout.setError("Required");
                flag = false;
            }
            if(TextUtils.isEmpty(descEditText.getText())) {
                descLayout.setError("Required");
                flag = false;
            }
            if(selectedPin == -1) {
                flag = false;
                pinSelectionErrorMessageText.setVisibility(View.VISIBLE);
            }
            if(flag) {
                viewModel.setPinData(selectedPin,
                        titleEditText.getText().toString(),
                        descEditText.getText().toString());
                dismiss();
            }
        }
    }

    @Override
    public void onClick(Integer value) {
        selectedPin = value;
        pinSelectionErrorMessageText.setVisibility(View.GONE);
    }
}
