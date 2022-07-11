package com.raushankit.ILghts.forms.board;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.viewModel.BoardFormViewModel;


public class BoardTitle extends Fragment {

    private static final String TAG = "BoardTitle";

    private BoardFormViewModel boardFormViewModel;
    private String[] visibilityOpt;

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
    }

    public static BoardTitle newInstance() {
        return new BoardTitle();
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
        visibilityOpt = getResources().getStringArray(R.array.board_form_visibility_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, visibilityOpt);
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
            Bundle args = new Bundle();
            args.putString(BoardFormConst.CHANGE_FRAGMENT, BoardFormConst.FORM2);
            getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boardFormViewModel = new ViewModelProvider(requireActivity()).get(BoardFormViewModel.class);
        boardFormViewModel.getBasicData().observe(getViewLifecycleOwner(), boardBasicModel -> {
            if(boardBasicModel == null) return;
            if(!TextUtils.isEmpty(boardBasicModel.getName())){
                nameEditText.setText(boardBasicModel.getName());
            }
            if(!TextUtils.isEmpty(boardBasicModel.getDescription())){
                descEditText.setText(boardBasicModel.getName());
            }
            if(boardBasicModel.getVisibility() != -1){
                visibilityText.setText(visibilityOpt[boardBasicModel.getVisibility()]);
            }
        });
    }

    private void saveData(){
        BoardBasicModel model = new BoardBasicModel();
        model.setName(String.valueOf(nameEditText.getText()));
        model.setVisibility(TextUtils.isEmpty(visibilityText.getText())?-1:(TextUtils.equals(visibilityText.getText(), visibilityOpt[0])?0:1));
        boardFormViewModel.setBasicModel(model);
    }

    @Override
    public void onResume() {
        super.onResume();
        nameEditText.addTextChangedListener(nameWatcher);
        descEditText.addTextChangedListener(descWatcher);
        visibilityText.addTextChangedListener(visibilityWatcher);
        Bundle args = new Bundle();
        args.putInt(BoardFormConst.CURRENT_FRAGMENT, 1);
        getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
        nameEditText.removeTextChangedListener(nameWatcher);
        descEditText.removeTextChangedListener(descWatcher);
        visibilityText.removeTextChangedListener(visibilityWatcher);
    }
}