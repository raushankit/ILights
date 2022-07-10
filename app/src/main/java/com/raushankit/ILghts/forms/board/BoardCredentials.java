package com.raushankit.ILghts.forms.board;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;


public class BoardCredentials extends Fragment {
    private static final String TAG = "BoardCredentials";

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
        BoardCredentials fragment = new BoardCredentials();
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

        prevButton.setOnClickListener(v -> {
            // TODO: implement this
        });

        nextButton.setOnClickListener(v -> {
            if(!checkCredentials()) return;
            // TODO: implement this
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        usernameText.addTextChangedListener(usernameWatcher);
        passwordText.addTextChangedListener(passwordWatcher);
    }

    private boolean checkCredentials(){
        boolean flag = true;
        if(TextUtils.isEmpty(usernameText.getText())){
            usernameLayout.setError(requiredString);
            flag = false;
        }
        if(TextUtils.isEmpty(passwordText.getText())){
            passwordLayout.setError(requiredString);
            flag = false;
        }
        return flag;
    }

    @Override
    public void onStop() {
        super.onStop();
        usernameText.removeTextChangedListener(usernameWatcher);
        passwordText.removeTextChangedListener(passwordWatcher);
    }
}