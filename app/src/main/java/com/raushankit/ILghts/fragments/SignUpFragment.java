package com.raushankit.ILghts.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.dialogs.LoadingDialogFragment;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.utils.UserUpdates;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SIGNUP_FRAGMENT";
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final UserUpdates userUpdates = new UserUpdates();
    private AlertDialogFragment alertDialogFragment;
    private LoadingDialogFragment loadingDialogFragment;
    private SharedRepo sharedRepo;
    private CallBack<PageKeys> changeFrags;

    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextInputLayout nameLayout;
    private TextInputEditText nameInput;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordInput;
    private TextInputLayout confPasswordLayout;
    private TextInputEditText confPasswordInput;
    private MaterialButton signupButton;
    private MaterialButton googleLoginButton;

    private TextWatcher emailWatcher;
    private TextWatcher nameWatcher;
    private TextWatcher passwordWatcher;
    private TextWatcher confPasswordWatcher;

    public SignUpFragment(){

    }

    public SignUpFragment(@NonNull CallBack<Integer> statusBarColor, CallBack<PageKeys> changeFrags) {
        statusBarColor.onClick(R.color.splash_screen_bg_end);
        this.changeFrags = changeFrags;
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        alertDialogFragment = AlertDialogFragment.newInstance("SignUp Failed",
                true, false);
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if(whichButton == AlertDialogFragment.WhichButton.POSITIVE){
                alertDialogFragment.dismiss();
            }else{
                Log.w(TAG, "SignUpFragment: negative button clicked");
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Work Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedRepo = SharedRepo.newInstance(requireContext());
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initialize();
        signupButton.setOnClickListener(v -> {
            boolean isEmailBad = TextUtils.isEmpty(emailInput.getText());
            boolean isNameBad = TextUtils.isEmpty(nameInput.getText());
            boolean isPasswordBad = TextUtils.isEmpty(passwordInput.getText());
            boolean isConfirmPasswordBad = TextUtils.isEmpty(confPasswordInput.getText());
            if(isEmailBad || isNameBad || isPasswordBad || isConfirmPasswordBad ){
                if(isEmailBad) emailLayout.setError(getString(R.string.required));
                if(isNameBad) nameLayout.setError(getString(R.string.required));
                if(isPasswordBad) passwordLayout.setError(getString(R.string.required));
                if(isConfirmPasswordBad) confPasswordLayout.setError(getString(R.string.required));
            }else{
                String pw = Objects.requireNonNull(passwordInput.getText()).toString();
                String cpw = Objects.requireNonNull(confPasswordInput.getText()).toString();
                if(pw.equals(cpw)){
                    loadingDialogFragment.setTitle(getString(R.string.signing_up));
                    loadingDialogFragment.show(getChildFragmentManager(),LoadingDialogFragment.TAG);
                    createUser(Objects.requireNonNull(emailInput.getText()).toString(),
                            Objects.requireNonNull(nameInput.getText()).toString(), pw);
                }else{
                    confPasswordLayout.setError(getString(R.string.password_not_match));
                }
            }
        });
        googleLoginButton.setOnClickListener(v -> changeFrags.onClick(PageKeys.GOOGLE_LOGIN_EVENT));
        return view;
    }

    private void createUser(@NonNull String email, @NonNull String name, @NonNull String password) {
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, null);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    try{
                        FirebaseUser user = task.getResult().getUser();
                        if(user != null && task.isSuccessful()){
                            Map<SharedRefKeys, String> mp = new LinkedHashMap<>();
                            mp.put(SharedRefKeys.AUTH_TYPE, "EMAIL");
                            mp.put(SharedRefKeys.AUTH_SUCCESSFUL, Boolean.FALSE.toString());
                            mp.put(SharedRefKeys.USER_EMAIL, user.getEmail());
                            mp.put(SharedRefKeys.USER_NAME, name);
                            sharedRepo.insert(mp);
                            updateMetadata(mp, user.getUid());
                        }else{
                            loadingDialogFragment.dismiss();
                            showAlert(task.getException());
                        }
                    }catch (Exception e){
                        loadingDialogFragment.dismiss();
                        showAlert(null);
                    }
                });
    }

    private void initialize() {
        emailLayout = view.findViewById(R.id.signup_frag_email_input_layout);
        emailInput = view.findViewById(R.id.signup_frag_email_input_edit_text);
        nameLayout = view.findViewById(R.id.signup_frag_name_input_layout);
        nameInput = view.findViewById(R.id.signup_frag_name_input_edit_text);
        passwordLayout = view.findViewById(R.id.signup_frag_password_input_layout);
        passwordInput = view.findViewById(R.id.signup_frag_password_input_edit_text);
        confPasswordLayout = view.findViewById(R.id.signup_frag_confirm_password_input_layout);
        confPasswordInput = view.findViewById(R.id.signup_frag_confirm_password_input_edit_text);
        signupButton = view.findViewById(R.id.signup_frag_login_button);
        googleLoginButton = view.findViewById(R.id.signup_frag_google_login_btn);

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
        confPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void updateMetadata(@NonNull Map<SharedRefKeys, String> mp, @NonNull String uid) {
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user1->{
            if(user1 != null){
                loadingDialogFragment.dismiss();
                sharedRepo.insert(SharedRefKeys.AUTH_SUCCESSFUL, Boolean.TRUE.toString());
                changeFrags.onClick(PageKeys.CONTROLLER_PAGE);
            }else{
                String errorStr = userUpdates.createUserMetaData(uid, new User(mp.get(SharedRefKeys.USER_NAME),mp.get(SharedRefKeys.USER_EMAIL)));
                if(errorStr != null){
                    loadingDialogFragment.dismiss();
                    showAlert(new Exception(errorStr));
                }
            }
        });
    }

    public void showAlert(Exception exception){
        alertDialogFragment.setBodyString(exception==null || exception.getMessage()==null?getString(R.string.internet_connection_error):exception.getMessage());
        alertDialogFragment.show(getChildFragmentManager(),AlertDialogFragment.TAG);
    }

    /////////////////////////////////////////////


    @Override
    public void onResume() {
        super.onResume();
        emailInput.addTextChangedListener(emailWatcher);
        nameInput.addTextChangedListener(nameWatcher);
        passwordInput.addTextChangedListener(passwordWatcher);
        confPasswordInput.addTextChangedListener(confPasswordWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        emailInput.removeTextChangedListener(emailWatcher);
        nameInput.removeTextChangedListener(nameWatcher);
        passwordInput.removeTextChangedListener(passwordWatcher);
        confPasswordInput.removeTextChangedListener(confPasswordWatcher);
    }
}