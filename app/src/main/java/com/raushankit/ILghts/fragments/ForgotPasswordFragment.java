package com.raushankit.ILghts.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.Objects;

public class ForgotPasswordFragment extends Fragment {

    private FirebaseAuth mAuth;
    private CallBack<PageKeys> changeFrag;

    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;

    private TextWatcher emailWatcher;

    public ForgotPasswordFragment(){

    }

    public ForgotPasswordFragment(@NonNull CallBack<Integer> statusBarColor, @NonNull CallBack<PageKeys> changeFrag) {
        statusBarColor.onClick(R.color.splash_screen_bg_end);
        this.changeFrag = changeFrag;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        emailLayout = view.findViewById(R.id.forgot_password_frag_email_input_layout);
        emailInput = view.findViewById(R.id.forgot_password_frag_email_input_edit_text);
        MaterialButton sendButton = view.findViewById(R.id.forgot_password_frag_submit_button);

        emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        sendButton.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(emailInput.getText())){
                mAuth.sendPasswordResetEmail(Objects.requireNonNull(emailInput.getText()).toString())
                        .addOnCompleteListener(task -> {
                            Snackbar.make(view, (getString(task.isSuccessful()?R.string.forgot_password_email_sent_success:R.string.forgot_password_email_send_failure)), BaseTransientBottomBar.LENGTH_SHORT).show();
                            changeFrag.onClick(PageKeys.LOGIN_PAGE);
                        });
            }else{
                emailLayout.setError(getString(R.string.required));
            }
            if(mAuth.getCurrentUser() != null) mAuth.signOut();
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        emailInput.addTextChangedListener(emailWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        emailInput.removeTextChangedListener(emailWatcher);
    }
}