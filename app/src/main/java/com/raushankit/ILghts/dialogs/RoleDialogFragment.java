package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.AdminUser;
import com.raushankit.ILghts.model.RoleModData;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.viewModel.RoleDialogViewModel;

public class RoleDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "RoleDialogFragment";
    private static final String MY_ROLE = "my_role";
    private static final String USER_ROLE = "user_role";
    private static final String USER_UID = "user_uid";
    private static final String USER_NAME = "user_name";
    private static final String USER_EMAIL = "user_email";

    private RoleDialogViewModel roleDialogViewModel;
    private final RadioButton[] radioButtons = new RadioButton[4];
    private RadioGroup radioGroup;

    private String uid;
    private final User user = new User();
    private int myLevel;
    private int userLevel;

    public RoleDialogFragment() {
        Log.w(TAG, "ManageUserFragment: empty constructor");
    }

    public static RoleDialogFragment newInstance(int myRole) {
        RoleDialogFragment fragment = new RoleDialogFragment();
        Bundle args = new Bundle();
        args.putInt(MY_ROLE, myRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView: inside on create");
        View view = inflater.inflate(R.layout.fragment_dialog_role_editor, container, false);
        TextView nameView = view.findViewById(R.id.frag_dialog_role_name);
        TextView emailView = view.findViewById(R.id.frag_dialog_role_email);
        TextView roleView = view.findViewById(R.id.frag_dialog_current_level);
        radioGroup = view.findViewById(R.id.role_dialog_radio_group);
        radioButtons[0] = radioGroup.findViewById(R.id.role_dialog_radio_block);
        radioButtons[1] = radioGroup.findViewById(R.id.role_dialog_radio_user);
        radioButtons[2] = radioGroup.findViewById(R.id.role_dialog_radio_admin);
        radioButtons[3] = radioGroup.findViewById(R.id.role_dialog_radio_developer);
        MaterialButton confirmButton = view.findViewById(R.id.role_dialog_positive_button);
        MaterialButton cancelButton = view.findViewById(R.id.role_dialog_negative_button);
        String[] userLevels = getResources().getStringArray(R.array.user_types);
        Bundle args = getArguments();
        if (args != null) {
            myLevel = args.getInt(MY_ROLE);
            userLevel = args.getInt(USER_ROLE);
            uid = args.getString(USER_UID);
            user.setName(args.getString(USER_NAME));
            user.setEmail(args.getString(USER_EMAIL));
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
            roleView.setText(getString(R.string.role_dialog_role_level, userLevels[Math.max(0,Math.min(userLevel, userLevels.length))]));
            enableRadios(userLevel);
        }
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
        radioGroup.clearCheck();
        radioGroup.check(radioButtons[Math.max(0,Math.min(userLevel, 3))].getId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roleDialogViewModel = new ViewModelProvider(requireParentFragment()).get(RoleDialogViewModel.class);
    }

    public void setAdminUser(int userRole, @NonNull AdminUser adminUser){
        Bundle args = getArguments();
        if(args == null) return;
        args.putInt(USER_ROLE, userRole);
        args.putString(USER_UID, adminUser.getUid());
        args.putString(USER_EMAIL, adminUser.getUser().getEmail());
        args.putString(USER_NAME, adminUser.getUser().getName());
        setArguments(args);
    }

    private void enableRadios(int userLevel) {
        for (int i = 0; i < 4; ++i) {
            radioButtons[i].setEnabled(false);
        }
        if (userLevel < myLevel) {
            for (int i = 0; i < myLevel; ++i) {
                radioButtons[i].setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.role_dialog_positive_button) {
            int id = radioGroup.getCheckedRadioButtonId();
            int level;
            if (id == R.id.role_dialog_radio_block) level = 0;
            else if (id == R.id.role_dialog_radio_user) level = 1;
            else if (id == R.id.role_dialog_radio_admin) level = 2;
            else if (id == R.id.role_dialog_radio_developer) level = 3;
            else level = userLevel;
            if (userLevel != level) roleDialogViewModel.setData(new RoleModData(uid, level));
        }
        dismiss();
    }
}
