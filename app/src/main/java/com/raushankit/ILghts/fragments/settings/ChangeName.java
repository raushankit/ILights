package com.raushankit.ILghts.fragments.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.R;

import java.util.Locale;

public class ChangeName extends Fragment {

    private static final String ARG_PARAM1 = "prev_name";
    private static final String ARG_PARAM2 = "user_uid";

    private String prevUserName;
    private String userUid;
    private TextInputLayout nameLayout;
    private TextInputEditText nameText;
    private ProgressBar circularProgressBar;

    private TextWatcher nameWatcher;

    public ChangeName() {
        // Required empty public constructor
    }

    public static ChangeName newInstance(String prevUserName, String userUid) {
        ChangeName fragment = new ChangeName();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, prevUserName);
        args.putString(ARG_PARAM2, userUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prevUserName = getArguments().getString(ARG_PARAM1);
            userUid = getArguments().getString(ARG_PARAM2);
        }
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_name, container, false);
        nameLayout = view.findViewById(R.id.change_name_frag_name_input_layout);
        nameText = view.findViewById(R.id.change_name_frag_name_input_edit_text);
        MaterialButton update = view.findViewById(R.id.change_name_frag_login_button);
        circularProgressBar = view.findViewById(R.id.change_name_circular_progress);
        circularProgressBar.setVisibility(View.GONE);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/"+userUid+"/name");

        if(!TextUtils.isEmpty(prevUserName)){
            nameText.setText(prevUserName);
        }

        update.setOnClickListener(v -> {
            if(nameText.getText()!= null && TextUtils.isEmpty(nameText.getText())){
                nameLayout.setError(getString(R.string.required));
                nameText.setText(prevUserName);
            }else{
                if(!TextUtils.equals(nameText.getText(), prevUserName)){
                    circularProgressBar.setVisibility(View.VISIBLE);
                    db.setValue(nameText.getText().toString().toLowerCase(Locale.ROOT))
                            .addOnCompleteListener(task -> {
                                Bundle result = new Bundle();
                                result.putString("result",
                                        task.isSuccessful()?"name updated successfully!":"failed to update name!");
                                getParentFragmentManager().setFragmentResult("request", result);
                                getParentFragmentManager().popBackStackImmediate();
                            });
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        nameText.addTextChangedListener(nameWatcher);
        super.onResume();
    }

    @Override
    public void onStop() {
        nameText.removeTextChangedListener(nameWatcher);
        super.onStop();
    }
}