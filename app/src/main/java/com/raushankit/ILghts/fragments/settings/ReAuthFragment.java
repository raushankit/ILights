package com.raushankit.ILghts.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.viewModel.SettingCommViewModel;

import java.util.Objects;

public class ReAuthFragment extends Fragment {
    private static final String TAG = "ReAuthFragment";

    private View view;
    private FirebaseUser user;
    private ActivityResultLauncher<Intent> resultLauncher;
    private SettingCommViewModel settingCommViewModel;
    private boolean isActionChange;
    private boolean isProviderGoogle;
    private TextInputEditText reAuthPasswordEditText;
    private TextInputEditText changePWEditText;
    private TextInputEditText confirmPWEditText;

    private TextInputLayout reAuthPasswordLayout;
    private TextInputLayout changePWLayout;
    private TextInputLayout confirmPWLayout;
    private ProgressBar circularProgress;
    private MaterialButton reAuthButton;
    private MaterialButton negativeButton;
    private MaterialButton positiveButton;
    private LinearLayout parentChangeLayout;
    private TextView reAuthConfirmView;

    private TextWatcher reAuthPasswordTextWatcher;
    private TextWatcher changePWTextWatcher;
    private TextWatcher confirmPWTextWatcher;


    public ReAuthFragment() {
        // Required empty public constructor
    }

    public static ReAuthFragment newInstance() {
        return new ReAuthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isActionChange = getArguments().getString("action").equals("change_password");
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        isProviderGoogle = getProviderData();
    }

    private boolean getProviderData() {
        if(user == null) return false;
        for(UserInfo userInfo : user.getProviderData()){
            if(userInfo.getProviderId().equals("google.com")) return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_re_auth, container, false);
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(),gso);
        reAuthButton.setOnClickListener(v -> {
            if(isProviderGoogle){
                circularProgress.setVisibility(View.VISIBLE);
                resultLauncher.launch(mGoogleSignInClient.getSignInIntent());
            }else{
                if(TextUtils.isEmpty(reAuthPasswordEditText.getText())){
                    reAuthPasswordLayout.setError(getString(R.string.required));
                    return;
                }
                circularProgress.setVisibility(View.VISIBLE);
                AuthCredential emailCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), Objects.requireNonNull(reAuthPasswordEditText.getText()).toString());
                reAuthByCredential(emailCredential);
            }
            reAuthButton.setEnabled(false);
        });

        positiveButton.setOnClickListener(v -> {
            if(isActionChange){
                boolean invalidPW = TextUtils.isEmpty(changePWEditText.getText());
                boolean invalidConfPW = TextUtils.isEmpty(confirmPWEditText.getText());
                if(invalidConfPW || invalidPW){
                    if(invalidConfPW) confirmPWLayout.setError(getString(R.string.required));
                    if(invalidPW) changePWLayout.setError(getString(R.string.required));
                    return;
                }
                if(!TextUtils.equals(changePWEditText.getText(), confirmPWEditText.getText())){
                    confirmPWLayout.setError(getString(R.string.password_not_match));
                    return;
                }
                settingCommViewModel.selectItem(new Pair<>("update_password", Objects.requireNonNull(changePWEditText.getText()).toString()));
            }else{
                settingCommViewModel.selectItem(new Pair<>("delete_user", null));
            }
        });
        negativeButton.setOnClickListener(v -> settingCommViewModel.selectItem(new Pair<>("remove_fragment", null)));

        return view;
    }

    private void init() {
        TextView title = view.findViewById(R.id.frag_re_auth_title);
        circularProgress = view.findViewById(R.id.frag_re_auth_circular_progress);
        TextView emailText = view.findViewById(R.id.frag_re_auth_email);
        TextView deleteTitle = view.findViewById(R.id.frag_re_auth_delete_user_message);
        reAuthConfirmView = view.findViewById(R.id.frag_re_auth_confirmation_message);
        reAuthPasswordLayout = view.findViewById(R.id.frag_re_auth_password_input_layout);
        reAuthPasswordEditText = view.findViewById(R.id.frag_re_auth_password_input_edit_text);
        changePWLayout = view.findViewById(R.id.frag_re_auth_change_password_input_layout);
        changePWEditText = view.findViewById(R.id.frag_re_auth_change_password_input_edit_text);
        confirmPWLayout = view.findViewById(R.id.frag_re_auth_change_confirm_password_input_layout);
        confirmPWEditText = view.findViewById(R.id.frag_re_auth_change_confirm_password_input_edit_text);
        parentChangeLayout = view.findViewById(R.id.frag_re_auth_change_password_parent);
        reAuthButton = view.findViewById(R.id.frag_re_auth_button);
        negativeButton = view.findViewById(R.id.frag_re_auth_negative_btn);
        positiveButton = view.findViewById(R.id.frag_re_auth_positive_btn);

        reAuthPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reAuthPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        changePWTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changePWLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        confirmPWTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPWLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        title.setText(isActionChange?R.string.change_password:R.string.delete_account);
        reAuthPasswordLayout.setVisibility(isProviderGoogle?View.GONE:View.VISIBLE);
        changePWLayout.setVisibility(isActionChange?View.VISIBLE:View.GONE);
        confirmPWLayout.setVisibility(isActionChange?View.VISIBLE:View.GONE);
        deleteTitle.setVisibility(isActionChange?View.GONE:View.VISIBLE);
        positiveButton.setText(isActionChange? R.string.change: R.string.delete);
        parentChangeLayout.setVisibility(View.GONE);
        emailText.setText(user.getEmail());
        reAuthButton.setIcon(ResourcesCompat.getDrawable(getResources(), (isProviderGoogle?R.drawable.ic_google_colored:R.drawable.ic_baseline_email_24), requireContext().getTheme()));

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            assert account != null;
                            AuthCredential googleCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            reAuthByCredential(googleCredential);
                        } catch (ApiException e) {
                            Snackbar.make(view, (e.getMessage()==null?getString(R.string.re_auth,"failed"):e.getMessage()), BaseTransientBottomBar.LENGTH_SHORT).show();
                            circularProgress.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Snackbar.make(view, getString(R.string.re_auth,"failed"), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
    }

    private void reAuthByCredential(AuthCredential credential){
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    reAuthButton.setEnabled(!task.isSuccessful());
                    reAuthPasswordLayout.setVisibility(task.isSuccessful()? View.GONE: View.VISIBLE);
                    if(task.isSuccessful()){
                        parentChangeLayout.setVisibility(View.VISIBLE);
                        reAuthConfirmView.setVisibility(View.VISIBLE);
                        Log.w(TAG, "reAuthByCredential: reAuth successful");
                    }else{
                        Snackbar.make(view, (task.getException() != null && task.getException().getMessage() != null? task.getException().getMessage(): getString(R.string.re_auth,"failed")), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    circularProgress.setVisibility(View.GONE);
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingCommViewModel = new ViewModelProvider(requireActivity()).get(SettingCommViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        reAuthPasswordEditText.addTextChangedListener(reAuthPasswordTextWatcher);
        changePWEditText.addTextChangedListener(changePWTextWatcher);
        confirmPWEditText.addTextChangedListener(confirmPWTextWatcher);
    }

    @Override
    public void onStop() {
        reAuthPasswordEditText.removeTextChangedListener(reAuthPasswordTextWatcher);
        changePWEditText.removeTextChangedListener(changePWTextWatcher);
        confirmPWEditText.removeTextChangedListener(confirmPWTextWatcher);
        super.onStop();
    }
}