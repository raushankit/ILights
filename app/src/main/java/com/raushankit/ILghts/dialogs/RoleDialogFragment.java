package com.raushankit.ILghts.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.utils.callbacks.CallBack;

public class RoleDialogFragment extends DialogFragment implements View.OnClickListener{
    public static final String TAG = "RoleDialogFragment";

    private final RadioButton[] radioButtons = new RadioButton[4];
    private CallBack<Pair<String, Integer>> callBack;
    private RadioGroup radioGroup;

    private User user;
    private Role role;
    private String uid;
    private int yourLevel;

    RoleDialogFragment(){

    }

    public static RoleDialogFragment newInstance(int role){
        RoleDialogFragment fragment = new RoleDialogFragment();
        Bundle args = new Bundle();
        args.putInt("role", role);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: inside on create");
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

        if(getArguments() != null){
            yourLevel = getArguments().getInt("role");
        }

        if(user != null){
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
        }

        if(role != null){
            roleView.setText(getString(R.string.role_dialog_role_level, userLevels[role.getAccessLevel()]));
            enableRadios(role.getAccessLevel());
        }

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        super.onStart();
    }

    public void addCallback(@NonNull CallBack<Pair<String, Integer>> callBack){
        this.callBack = callBack;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private void enableRadios(int userLevel){
        for(int i = 0;i < 4;++i){
            radioButtons[i].setEnabled(false);
        }
        if(userLevel < yourLevel){
            for(int i = 0;i < Math.max(3,yourLevel);++i){
                radioButtons[i].setEnabled(true);
            }
        }
    }

    public int getRadioId(){
        int level = (role.getAccessLevel() + 4)%4;
        if(level<=0){
            return R.id.role_dialog_radio_block;
        }
        else if(level==1){
            return R.id.role_dialog_radio_user;
        }
        else if(level==2){
            return R.id.role_dialog_radio_admin;
        }
        else {
            return R.id.role_dialog_radio_developer;
        }
    }

    @Override
    public void onClick(View view) {
        if(callBack != null && view.getId() == R.id.role_dialog_positive_button){
            int id = radioGroup.getCheckedRadioButtonId();
            int level;
            if(id == R.id.role_dialog_radio_block)level = 0;
            else if(id == R.id.role_dialog_radio_user)level = 1;
            else if(id == R.id.role_dialog_radio_admin)level = 2;
            else if(id == R.id.role_dialog_radio_developer)level = 3;
            else level = role.getAccessLevel();
            if(role.getAccessLevel() != level) callBack.onClick(new Pair<>(uid,level));
        }
        radioGroup.clearCheck();
        dismiss();
    }
}
