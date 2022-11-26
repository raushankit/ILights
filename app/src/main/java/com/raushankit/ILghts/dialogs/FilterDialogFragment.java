package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.utils.NoFilterArrayAdapter;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "FilterDialogFragment";
    private final Map<String, Integer> mp = new HashMap<>();
    private CallBack<FilterModel> callBack;
    private FilterModel filterModel;

    private TextInputLayout fieldInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout timeInputLayout;

    private AutoCompleteTextView fieldInput;
    private TextInputEditText emailInput;
    private TextInputEditText nameInput;
    private AutoCompleteTextView timeInput;

    private final TextWatcher fieldWatcher;
    private final TextWatcher nameWatcher;
    private final TextWatcher emailWatcher;
    private final TextWatcher timeWatcher;

    public FilterDialogFragment() {
        fieldWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fieldInputLayout.setErrorEnabled(false);
                fieldInputLayout.setError(null);
            }

            @SuppressWarnings("all")
            @Override
            public void afterTextChanged(Editable s) {
                filterModel.setFieldName(fieldInput.getText().toString());
                filterModel.setFieldIndex(mp.getOrDefault(filterModel.getFieldName(), -1));
                nameInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 0
                        ? View.VISIBLE: View.GONE);
                emailInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 1
                        ? View.VISIBLE: View.GONE);
                timeInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 2
                        ? View.VISIBLE: View.GONE);
            }
        };
        nameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setErrorEnabled(false);
                nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterModel.setValue(s == null? null: s.toString());
            }
        };
        emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setErrorEnabled(false);
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterModel.setValue(s == null? null: s.toString());
            }
        };
        timeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeInputLayout.setErrorEnabled(false);
                timeInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterModel.setValue(s.toString());
            }
        };
    }

    public static FilterDialogFragment newInstance() {
        final FilterDialogFragment fragment = new FilterDialogFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_search_filter_dialog, container, false);
        MaterialButton clearBtn = view.findViewById(R.id.filter_dialog_clear_button);
        MaterialButton negativeBtn = view.findViewById(R.id.filter_dialog_negative_button);
        MaterialButton positiveBtn = view.findViewById(R.id.filter_dialog_positive_button);
        fieldInputLayout = view.findViewById(R.id.filter_board_search_dialog_menu);
        emailInputLayout = view.findViewById(R.id.filter_board_email_input_layout);
        nameInputLayout = view.findViewById(R.id.filter_board_name_input_layout);
        timeInputLayout = view.findViewById(R.id.filter_board_time_input_layout);
        fieldInput = view.findViewById(R.id.filter_board_search_dialog_text);
        emailInput = view.findViewById(R.id.filter_board_email_input_edit_text);
        nameInput = view.findViewById(R.id.filter_board_name_input_edit_text);
        timeInput = view.findViewById(R.id.filter_board_time_input_edit_text);
        TextView body = view.findViewById(R.id.filter_board_search_dialog_body);
        Spanned spanned = HtmlCompat.fromHtml(getString(R.string.filter_dialog_body), HtmlCompat.FROM_HTML_MODE_LEGACY);
        body.setText(spanned);
        negativeBtn.setOnClickListener(this);
        positiveBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        String[] fields = getResources().getStringArray(R.array.filter_model_board_search_fields);
        for(int i = 0;i < fields.length;++i) {
            mp.put(fields[i], i);
        }
        NoFilterArrayAdapter<String> fieldAdapter = new NoFilterArrayAdapter<>(requireContext(), R.layout.dropdown_item, fields);
        fieldInput.setAdapter(fieldAdapter);
        String[] timeOrders = getResources().getStringArray(R.array.filter_model_time_order);
        NoFilterArrayAdapter<String> timeAdapter = new NoFilterArrayAdapter<>(requireContext(), R.layout.dropdown_item, timeOrders);
        timeInput.setAdapter(timeAdapter);
        if(filterModel != null) {
            Log.e(TAG, "onCreateView: model in bundle " + filterModel);
            if(filterModel.getFieldName() != null) {
                fieldInput.setText(filterModel.getFieldName(), false);
                nameInput.setText(filterModel.getFieldIndex() == 0? filterModel.getValue(): null);
                emailInput.setText(filterModel.getFieldIndex() == 1? filterModel.getValue(): null);
                timeInput.setText(filterModel.getFieldIndex() == 2? filterModel.getValue(): null, false);
            } else {
                fieldInput.getText().clear();
                nameInput.setText(null);
                emailInput.setText(null);
                timeInput.getText().clear();
            }
            nameInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 0
                    ? View.VISIBLE: View.GONE);
            emailInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 1
                    ? View.VISIBLE: View.GONE);
            timeInputLayout.setVisibility(filterModel.getFieldName() != null && filterModel.getFieldIndex() == 2
                    ? View.VISIBLE: View.GONE);
        }
        return view;
    }


    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        super.onStart();
    }

    public void addCallBack(@NonNull CallBack<FilterModel> callBack) {
        this.callBack = callBack;
    }

    public void setFilterModel(@NonNull FilterModel model) {
        filterModel = model.clone();
    }

    @Override
    public void onClick(View view) {
        if (callBack == null) {
            dismiss();
        } else {
            int id = view.getId();
            if(id == R.id.filter_dialog_positive_button) {
                if(filterModel.getFieldName() == null) {
                    fieldInputLayout.setErrorEnabled(true);
                    fieldInputLayout.setError(getString(R.string.no_field_chosen));
                } else {
                    boolean flag = true;
                    if(filterModel.getFieldIndex() == 0) {
                        filterModel.setValue(filterModel.getValue() == null? null: filterModel.getValue().trim());
                        nameInput.setText(filterModel.getValue());
                        if(filterModel.getValue() == null || filterModel.getValue().length() < 4){
                            nameInputLayout.setErrorEnabled(true);
                            nameInputLayout.setError(getString(filterModel.getValue() == null
                                    ? R.string.type_something_to_search
                                    : R.string.type_more_than_3_words));
                            flag = false;
                        }
                    } else if(filterModel.getFieldIndex() == 1) {
                        filterModel.setValue(filterModel.getValue() == null? null: filterModel.getValue().trim());
                        emailInput.setText(filterModel.getValue());
                        if(filterModel.getValue() == null || filterModel.getValue().length() < 4){
                            emailInputLayout.setErrorEnabled(true);
                            emailInputLayout.setError(getString(filterModel.getValue() == null
                                    ? R.string.type_something_to_search
                                    : R.string.type_more_than_3_words));
                            flag = false;
                        }
                    } else if(filterModel.getFieldIndex() == 2) {
                        if(filterModel.getValue() == null){
                            timeInputLayout.setErrorEnabled(true);
                            timeInputLayout.setError(getString(R.string.pick_an_order));
                            flag = false;
                        }
                    }
                    if(flag) {
                        filterModel.setType(FilterModel.Type.FIELD);
                        callBack.onClick(filterModel);
                        dismiss();
                    }
                }
            } else if(id == R.id.filter_dialog_clear_button) {
                filterModel = new FilterModel();
                filterModel.setType(FilterModel.Type.NULL);
                callBack.onClick(filterModel);
                dismiss();
                fieldInput.getText().clear();
            } else if (id == R.id.filter_dialog_negative_button) {
                dismiss();
            } else {
                Log.i(TAG, "onClick: unknown id = " + id);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fieldInput.addTextChangedListener(fieldWatcher);
        nameInput.addTextChangedListener(nameWatcher);
        emailInput.addTextChangedListener(emailWatcher);
        timeInput.addTextChangedListener(timeWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        fieldInput.removeTextChangedListener(fieldWatcher);
        nameInput.removeTextChangedListener(nameWatcher);
        emailInput.removeTextChangedListener(emailWatcher);
        timeInput.removeTextChangedListener(timeWatcher);
    }
}
