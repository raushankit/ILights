package com.raushankit.ILghts.forms.board;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.storage.VolleyRequest;
import com.raushankit.ILghts.viewModel.BoardFormViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class BoardCredentials extends Fragment {

    private static final String TAG = "BoardCredentials";
    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=";
    private View view;
    private BoardFormViewModel boardFormViewModel;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameText;
    private TextInputEditText passwordText;
    private TextWatcher usernameWatcher;
    private TextWatcher passwordWatcher;
    private MaterialButton prevButton;
    private MaterialButton nextButton;
    private String requiredString;
    private String usernameSuffix;
    private BoardCredentialModel model;
    private VolleyRequest requestQueue;
    private String apiKey;

    public BoardCredentials() {
        // Required empty public constructor
    }

    public static BoardCredentials newInstance(@NonNull String apiKey) {
        BoardCredentials fragment = new BoardCredentials();
        Bundle args = new Bundle();
        args.putString(BoardFormConst.API_KEY, apiKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = VolleyRequest.newInstance(requireActivity());
        model = new BoardCredentialModel();
        Bundle args = getArguments();
        assert args != null;
        apiKey = args.getString(BoardFormConst.API_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_board_credentials, container, false);
        usernameLayout = view.findViewById(R.id.board_form_credential_username_layout);
        usernameText = view.findViewById(R.id.board_form_credential_username_input);
        passwordLayout = view.findViewById(R.id.board_form_credential_password_layout);
        passwordText = view.findViewById(R.id.board_form_credential_password_input);
        init(view);
        return view;
    }

    private void init(View view) {
        RelativeLayout navLayout = view.findViewById(R.id.board_form_credential_nav_button_layout);
        prevButton = navLayout.findViewById(R.id.form_navigation_prev_button);
        nextButton = navLayout.findViewById(R.id.form_navigation_next_button);
        requiredString = getString(R.string.required);
        usernameSuffix = getString(R.string.board_form_credential_username_suffix);

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
            if(TextUtils.isEmpty(model.getId())){
                sendProgressBarMessage(true);
                sendRequest();
            }else{
                Bundle args = new Bundle();
                args.putString(BoardFormConst.CHANGE_FRAGMENT, BoardFormConst.FORM4);
                getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
            }
        });
    }

    private void saveData(){
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
            if(!TextUtils.isEmpty(boardCredentialModel.getId())){
                usernameText.setInputType(InputType.TYPE_NULL);
                passwordText.setInputType(InputType.TYPE_NULL);
                model.setId(boardCredentialModel.getId());
            }else{
                usernameText.setInputType(InputType.TYPE_CLASS_TEXT);
                passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    private void sendProgressBarMessage(boolean show){
        Bundle args = new Bundle();
        args.putBoolean(BoardFormConst.PROGRESS_BAR, show);
        if(show){
            args.putInt(BoardFormConst.PROGRESS_TITLE, R.string.board_form_verifying_credentials);
            args.putInt(BoardFormConst.PROGRESS_BODY, R.string.please_wait);
        }
        getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
        prevButton.setEnabled(!show);
        nextButton.setEnabled(!show);
        usernameText.setInputType(show?InputType.TYPE_NULL:InputType.TYPE_CLASS_TEXT);
        passwordText.setInputType(show?InputType.TYPE_NULL:(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
    }

    private void sendRequest(){
        JSONObject js = new JSONObject();
        try{
           js.put("email", usernameText.getText() + usernameSuffix);
           js.put("password", String.valueOf(passwordText.getText()));
           js.put("returnSecureToken", true);
        }catch (JSONException e){
            Log.w(TAG, "sendRequest: ", e);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + apiKey, js, response -> {
            try{
                model.setId(response.getString("localId"));
                model.setRefreshToken(response.getString("refreshToken"));
                model.setIdToken(response.getString("idToken"));
                Bundle args = new Bundle();
                args.putString(BoardFormConst.CHANGE_FRAGMENT, BoardFormConst.FORM4);
                args.putBoolean(BoardFormConst.PROGRESS_BAR, false);
                args.putString(BoardFormConst.ID_TOKEN, model.getIdToken());
                getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
            } catch (JSONException e) {
                Log.w(TAG, "onResponse: ", e);
                Snackbar.make(view, R.string.unknown_error, BaseTransientBottomBar.LENGTH_SHORT).show();
                sendProgressBarMessage(false);
            }

        }, error -> {
            String errorString = getString(R.string.unknown_error);
            if(error.networkResponse == null){
                errorString = getString(R.string.internet_connection_error);
            }else{
                try{
                    JSONObject parent = new JSONObject(new String(error.networkResponse.data));
                    JSONObject errorChild = parent.getJSONObject("error");
                    errorString =  errorChild.getString("message");
                }catch (JSONException e){
                    Log.e(TAG, "onErrorResponse: ", e);
                }
            }
            Snackbar.make(view, errorString, BaseTransientBottomBar.LENGTH_SHORT).show();
            sendProgressBarMessage(false);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
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