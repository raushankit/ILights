package com.raushankit.ILghts.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private final UserUpdates userUpdates = new UserUpdates();
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isGoogleLogin;
    private CallBack<PageKeys> changeFrag;
    private SharedRepo sharedRepo;
    private AlertDialogFragment alertDialogFragment;
    private LoadingDialogFragment loadingDialogFragment;
    final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        loadingDialogFragment.setTitle(R.string.singing_in);
                        loadingDialogFragment.setMessage(R.string.please_wait);
                        loadingDialogFragment.show(getChildFragmentManager(), LoadingDialogFragment.TAG);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        assert account != null;
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        showAlert(e);
                    }
                } else {
                    showAlert(new Exception(getString(R.string.unknown_error)));
                }
            });
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton forgotPassButton;
    private MaterialButton googleLoginButton;
    private TextWatcher emailWatcher;
    private TextWatcher passwordWatcher;

    public LoginFragment() {

    }

    public LoginFragment(CallBack<PageKeys> changeFrag, boolean isGoogleLogin) {
        this.changeFrag = changeFrag;
        this.isGoogleLogin = isGoogleLogin;
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        alertDialogFragment = AlertDialogFragment.newInstance(R.string.login_failed, true, false);
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if (whichButton == AlertDialogFragment.WhichButton.POSITIVE) {
                alertDialogFragment.dismiss();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        Log.e(TAG, "LoginFragment: " + (mAuth.getCurrentUser() == null ? "no user" : mAuth.getCurrentUser().getEmail()));
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
        sharedRepo = SharedRepo.newInstance(requireContext());
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initiate();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        if (isGoogleLogin) {
            resultLauncher.launch(mGoogleSignInClient.getSignInIntent());
            isGoogleLogin = false;
        }
        googleLoginButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "google login");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
            resultLauncher.launch(mGoogleSignInClient.getSignInIntent());
        });

        loginButton.setOnClickListener(v -> {
            boolean isEmailCheck = TextUtils.isEmpty(emailInput.getText());
            boolean isPassCheck = TextUtils.isEmpty(passwordInput.getText());
            if (isEmailCheck || isPassCheck) {
                if (isEmailCheck) {
                    emailLayout.setError(getString(R.string.required));
                }
                if (isPassCheck) {
                    passwordLayout.setError(getString(R.string.required));
                }
            } else {
                loadingDialogFragment.setTitle(R.string.singing_in);
                loadingDialogFragment.setMessage(R.string.please_wait);
                loadingDialogFragment.show(getChildFragmentManager(), LoadingDialogFragment.TAG);
                signInWithEmailPassword(Objects.requireNonNull(emailInput.getText()).toString(),
                        Objects.requireNonNull(passwordInput.getText()).toString());
            }
        });
        forgotPassButton.setOnClickListener(v -> changeFrag.onClick(PageKeys.FORGOT_PASSWORD_PAGE));
        return view;
    }

    private void initiate() {
        emailLayout = view.findViewById(R.id.login_frag_email_input_layout);
        emailInput = view.findViewById(R.id.login_frag_email_input_edit_text);
        passwordLayout = view.findViewById(R.id.login_frag_password_input_layout);
        passwordInput = view.findViewById(R.id.login_frag_password_input_edit_text);
        forgotPassButton = view.findViewById(R.id.splash_screen_forgot_password_btn);
        loginButton = view.findViewById(R.id.login_frag_login_button);
        googleLoginButton = view.findViewById(R.id.login_frag_google_login_btn);

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
    }

    public void showAlert(Exception exception) {
        alertDialogFragment.setBodyString(exception == null || exception.getMessage() == null ? getString(R.string.internet_connection_error) : exception.getMessage());
        alertDialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
    }

    private void signInWithEmailPassword(String email, String password) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "username password login");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    try {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null && task.isSuccessful()) {
                            Map<SharedRefKeys, String> mp = new LinkedHashMap<>();
                            mp.put(SharedRefKeys.AUTH_TYPE, "EMAIL");
                            mp.put(SharedRefKeys.AUTH_SUCCESSFUL, Boolean.FALSE.toString());
                            mp.put(SharedRefKeys.USER_EMAIL, user.getEmail());
                            sharedRepo.insert(mp);
                            updateMetadata(user.getUid());
                        } else {
                            loadingDialogFragment.dismiss();
                            showAlert(task.getException());
                        }
                    } catch (Exception e) {
                        loadingDialogFragment.dismiss();
                        showAlert(e);
                    }
                });
    }

    private void updateMetadata(@NonNull String uid) {
        loadingDialogFragment.setMessage(R.string.verifying_user);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user1 -> {
            if (user1 != null) {
                loadingDialogFragment.dismiss();
                sharedRepo.insert(SharedRefKeys.AUTH_SUCCESSFUL, Boolean.TRUE.toString());
                changeFrag.onClick(PageKeys.CONTROLLER_PAGE);
            } else {
                String name = sharedRepo.getValue(SharedRefKeys.USER_NAME).toLowerCase(Locale.ROOT);
                String email = sharedRepo.getValue(SharedRefKeys.USER_EMAIL).toLowerCase(Locale.ROOT);
                String errorStr = userUpdates.createUserMetaData(uid, new User(name, email));
                if (errorStr != null) {
                    loadingDialogFragment.dismiss();
                    showAlert(new Exception(errorStr));
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    try {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null && task.isSuccessful()) {
                            Map<SharedRefKeys, String> mp = new LinkedHashMap<>();
                            mp.put(SharedRefKeys.AUTH_TYPE, "EMAIL");
                            mp.put(SharedRefKeys.AUTH_SUCCESSFUL, Boolean.FALSE.toString());
                            mp.put(SharedRefKeys.USER_EMAIL, user.getEmail());
                            mp.put(SharedRefKeys.USER_NAME, user.getDisplayName());
                            sharedRepo.insert(mp);
                            updateMetadata(user.getUid());
                        } else {
                            loadingDialogFragment.dismiss();
                            showAlert(task.getException());
                        }
                    } catch (Exception e) {
                        loadingDialogFragment.dismiss();
                        showAlert(e);
                    }
                });
    }

    /////////////////////////////////////////////

    @Override
    public void onResume() {
        super.onResume();
        emailInput.addTextChangedListener(emailWatcher);
        passwordInput.addTextChangedListener(passwordWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        emailInput.removeTextChangedListener(emailWatcher);
        passwordInput.removeTextChangedListener(passwordWatcher);
    }
}