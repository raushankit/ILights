package com.raushankit.ILghts.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.viewModel.SettingCommViewModel;

import java.util.Objects;

public class EditPinItemFragment extends Fragment {

    private static final String TAG = "EditPinItemFragment";

    private SettingCommViewModel settingCommViewModel;
    private String action;
    private int pinNumber;
    private String pinName;
    private TextInputEditText pinNameEdittext;
    private AlertDialogFragment alertDialogFragment;
    private TextWatcher textWatcher;
    private AutoCompleteTextView pinNumberText;
    private TextWatcher dropDownTextWatcher;
    private String[] pins;

    public EditPinItemFragment() {
        // Required empty public constructor
    }

    public static EditPinItemFragment newInstance(@NonNull String action, EditPinInfo pinInfo) {
        EditPinItemFragment fragment = new EditPinItemFragment();
        Bundle args = new Bundle();
        args.putString("action", action);
        args.putString("pin_name", pinInfo.getPinInfo().getName());
        args.putInt("pin_number", pinInfo.getPinNumber());
        fragment.setArguments(args);
        return fragment;
    }

    public static EditPinItemFragment newInstance(@NonNull String action, String[] pins) {
        EditPinItemFragment fragment = new EditPinItemFragment();
        Bundle args = new Bundle();
        args.putString("action", action);
        args.putStringArray("available_pins", pins);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            action = args.getString("action");
            pinName = args.getString("pin_name");
            pinNumber = args.getInt("pin_number");
            pins = args.getStringArray("available_pins");
        }
        alertDialogFragment = AlertDialogFragment.newInstance(R.string.confirm_action, true, true);
        alertDialogFragment.setBodyString(getString(R.string.sign_out_body_text));
        alertDialogFragment.setPositiveButtonText(R.string.yes);
        alertDialogFragment.setNegativeButtonText(R.string.alert_dialog_placeholder_btn_negative);
        alertDialogFragment.setCancelable(true);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Settings Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_pin_item, container, false);
        MaterialButton deleteButton = view.findViewById(R.id.frag_edit_pin_item_negative_btn);
        MaterialButton editButton = view.findViewById(R.id.frag_edit_pin_item_positive_btn);
        TextView title = view.findViewById(R.id.frag_edit_pin_item_title);
        TextInputLayout pinNumberLayout = view.findViewById(R.id.frag_edit_pin_item_pin_number_input_layout);
        TextInputLayout nameLayout = view.findViewById(R.id.frag_edit_pin_item_pin_name_layout);
        pinNumberText = view.findViewById(R.id.frag_edit_pin_item_pin_number_input_edit_text);
        pinNameEdittext = view.findViewById(R.id.frag_edit_pin_item_pin_name_edit_text);
        InputMethodManager im = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isEdit = action.equals("edit");
        deleteButton.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        editButton.setText(getString(isEdit ? R.string.change : R.string.add));
        pinNumberLayout.setEnabled(!isEdit);
        title.setText(isEdit ? "Edit Pin" : "Add Pin");
        if (isEdit) {
            pinNameEdittext.setText(pinName);
            pinNumberText.setText(getString(R.string.add_pin_frag_item_pin_number, pinNumber));
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.edit_pin_item_drop_down, pins);
            pinNumberText.setAdapter(adapter);
        }
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if (whichButton.equals(AlertDialogFragment.WhichButton.POSITIVE)) {
                settingCommViewModel.selectItem("delete_pin_item", pinNumber);
                getParentFragmentManager().popBackStack();
            } else {
                Log.w(TAG, "onCreateView: canceled delete operation");
            }
            alertDialogFragment.dismiss();
        });
        deleteButton.setOnClickListener(v -> {
            alertDialogFragment.setBodyString(getString(R.string.pin_delete_message, pinNumber));
            alertDialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
        });
        editButton.setOnClickListener(v -> {
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            boolean invalidPinName = TextUtils.isEmpty(pinNameEdittext.getText());
            boolean invalidPinNumber = TextUtils.isEmpty(pinNumberText.getText());
            if (invalidPinName || invalidPinNumber) {
                if (invalidPinName) nameLayout.setError(getString(R.string.required));
                if (invalidPinNumber) pinNumberLayout.setError(getString(R.string.required));
                return;
            }
            if (TextUtils.equals(pinName, pinNameEdittext.getText())) {
                nameLayout.setError(getString(R.string.same_as_before));
                return;
            }
            String[] str = pinNumberText.getText().toString().split(" ", -1);
            settingCommViewModel.selectItem((isEdit ? "edit_pin_item" : "add_pin_item"), new EditPinInfo(Integer.parseInt(str[str.length - 1]), new PinInfo(Objects.requireNonNull(pinNameEdittext.getText()).toString(), null)));
        });
        pinNameEdittext.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                boolean invalidPinName = TextUtils.isEmpty(pinNameEdittext.getText());
                boolean invalidPinNumber = TextUtils.isEmpty(pinNumberText.getText());
                if (invalidPinName || invalidPinNumber) {
                    if (invalidPinName) nameLayout.setError(getString(R.string.required));
                    if (invalidPinNumber) pinNumberLayout.setError(getString(R.string.required));
                    return false;
                }
                if (TextUtils.equals(pinName, pinNameEdittext.getText())) {
                    nameLayout.setError(getString(R.string.same_as_before));
                    return false;
                }
                String[] str = pinNumberText.getText().toString().split(" ", -1);
                settingCommViewModel.selectItem((isEdit ? "edit_pin_item" : "add_pin_item"), new EditPinInfo(Integer.parseInt(str[str.length - 1]), new PinInfo(Objects.requireNonNull(pinNameEdittext.getText()).toString(), null)));
                return true;
            }
            return false;
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        dropDownTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinNumberLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingCommViewModel = new ViewModelProvider(requireActivity()).get(SettingCommViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        pinNumberText.addTextChangedListener(dropDownTextWatcher);
        pinNameEdittext.addTextChangedListener(textWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        pinNumberText.removeTextChangedListener(dropDownTextWatcher);
        pinNameEdittext.removeTextChangedListener(textWatcher);
    }
}