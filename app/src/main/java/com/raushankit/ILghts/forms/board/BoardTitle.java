package com.raushankit.ILghts.forms.board;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;


public class BoardTitle extends Fragment {

    private static final String TAG = "BoardTitle";

    private TextInputLayout nameLayout;
    private TextInputLayout descLayout;
    private TextInputLayout visibilityLayout;
    private TextInputEditText nameEditText;
    private TextInputEditText descEditText;
    private TextWatcher nameWatcher;
    private TextWatcher descWatcher;
    private TextWatcher visibilityWatcher;
    private MaterialAutoCompleteTextView visibilityText;


    public BoardTitle() {
        // Required empty public constructor
    }

    public static BoardTitle newInstance() {
        BoardTitle fragment = new BoardTitle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_title, container, false);
        init(view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.board_form_visibility_options));
        visibilityText.setAdapter(adapter);

        return view;
    }

    private void init(View view){
        nameLayout = view.findViewById(R.id.board_form_title_frag_name_input_layout);
        nameEditText = view.findViewById(R.id.board_form_title_frag_name_input_edit_text);
        descLayout = view.findViewById(R.id.board_form_title_frag_description_input_layout);
        descEditText = view.findViewById(R.id.board_form_title_frag_description_input_edit_text);
        visibilityLayout = view.findViewById(R.id.board_form_title_frag_visibility_input_layout);
        visibilityText = view.findViewById(R.id.board_form_title_frag_visibility_input_edit_text);
        RelativeLayout navLayout = view.findViewById(R.id.board_form_title_frag_nav_button_layout);
        MaterialButton prevButton = navLayout.findViewById(R.id.form_navigation_prev_button);
        MaterialButton nextButton = navLayout.findViewById(R.id.form_navigation_next_button);
        prevButton.setEnabled(false);

        nameWatcher = new TextWatcher() {
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
        descWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        visibilityWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                visibilityLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        String required = getString(R.string.required);
        nextButton.setOnClickListener(v ->{
            if(checkIfEmpty(required)){return;}
            Log.d(TAG, "init: everything good");
        });
    }

    private boolean checkIfEmpty(String message){
        boolean flag = false;
        if(TextUtils.isEmpty(nameEditText.getText())){
            nameLayout.setError(message);
            flag = true;
        }
        if(TextUtils.isEmpty(descEditText.getText())){
            descLayout.setError(message);
            flag = true;
        }
        if(TextUtils.isEmpty(visibilityText.getText())){
            visibilityLayout.setError(message);
            flag = true;
        }
        return flag;
    }

    @Override
    public void onResume() {
        super.onResume();
        nameEditText.addTextChangedListener(nameWatcher);
        descEditText.addTextChangedListener(descWatcher);
        visibilityText.addTextChangedListener(visibilityWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        nameEditText.removeTextChangedListener(nameWatcher);
        descEditText.removeTextChangedListener(descWatcher);
        visibilityText.removeTextChangedListener(visibilityWatcher);
    }
}