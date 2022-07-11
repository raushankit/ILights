package com.raushankit.ILghts.forms.board;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.viewModel.BoardFormViewModel;


public class BoardCredentials extends Fragment {

    private static final String TAG = "BoardCredentials";
    private BoardFormViewModel boardFormViewModel;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameText;
    private TextInputEditText passwordText;
    private TextWatcher usernameWatcher;
    private TextWatcher passwordWatcher;
    private String requiredString;

    public BoardCredentials() {
        // Required empty public constructor
    }

    public static BoardCredentials newInstance() {
        return new BoardCredentials();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_credentials, container, false);
        usernameLayout = view.findViewById(R.id.board_form_credential_username_layout);
        usernameText = view.findViewById(R.id.board_form_credential_username_input);
        passwordLayout = view.findViewById(R.id.board_form_credential_password_layout);
        passwordText = view.findViewById(R.id.board_form_credential_password_input);
        init(view);
        return view;
    }

    private void init(View view) {
        RelativeLayout navLayout = view.findViewById(R.id.board_form_credential_nav_button_layout);
        MaterialButton prevButton = navLayout.findViewById(R.id.form_navigation_prev_button);
        MaterialButton nextButton = navLayout.findViewById(R.id.form_navigation_next_button);
        requiredString = getString(R.string.required);

        usernameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        prevButton.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate());

        nextButton.setOnClickListener(v -> {
            if(checkIfEmpty()) {
                Log.i(TAG, "init: empty credentials");
                return;
            }
            Bundle args = new Bundle();
            args.putString(BoardFormConst.CHANGE_FRAGMENT, BoardFormConst.FORM4);
            getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
        });
    }

    private void saveData(){
        BoardCredentialModel model = new BoardCredentialModel();
        model.setUsername(String.valueOf(usernameText.getText()));
        model.setPassword(String.valueOf(passwordText.getText()));
        boardFormViewModel.setCredentialModel(model);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boardFormViewModel = new ViewModelProvider(requireActivity()).get(BoardFormViewModel.class);

        boardFormViewModel.getCredentialsData().observe(getViewLifecycleOwner(), boardCredentialModel -> {
            if(boardCredentialModel == null) return;
            if(!TextUtils.isEmpty(boardCredentialModel.getUsername())){
                usernameText.setText(boardCredentialModel.getUsername());
            }
            if(!TextUtils.isEmpty(boardCredentialModel.getPassword())){
                passwordText.setText(boardCredentialModel.getPassword());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        usernameText.addTextChangedListener(usernameWatcher);
        passwordText.addTextChangedListener(passwordWatcher);
        Bundle args = new Bundle();
        args.putInt(BoardFormConst.CURRENT_FRAGMENT, 3);
        getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
    }

    private boolean checkIfEmpty(){
        boolean flag = false;
        if(TextUtils.isEmpty(usernameText.getText())){
            usernameLayout.setError(requiredString);
            flag = true;
        }
        if(TextUtils.isEmpty(passwordText.getText())){
            passwordLayout.setError(requiredString);
            flag = true;
        }
        return flag;
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
        usernameText.removeTextChangedListener(usernameWatcher);
        passwordText.removeTextChangedListener(passwordWatcher);
    }
}