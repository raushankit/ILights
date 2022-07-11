package com.raushankit.ILghts.forms.board;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.utils.NumberPicker;
import com.raushankit.ILghts.viewModel.BoardFormViewModel;

import java.util.ArrayList;
import java.util.List;

public class BoardVerification extends Fragment {
    private static final String TAG = "BoardVerification";
    private TextInputEditText nameText;
    private TextInputEditText descText;
    private TextInputEditText visibilityText;
    private String[] visibilityOpt;
    private TextInputEditText boardTypeText;
    private TextInputEditText noOfPinsText;
    private TextInputEditText usernameText;
    private TextInputEditText passwordText;
    private NumberPicker numberPicker;


    public BoardVerification() {
        // Required empty public constructor
    }
    public static BoardVerification newInstance() {
        return new BoardVerification();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visibilityOpt = getResources().getStringArray(R.array.board_form_visibility_options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_verification, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        initViews(view);
    }

    private void initViews(View view) {
        MaterialTextView basicSubtitle = view.findViewById(R.id.board_form_verification_subtitle_details);
        MaterialTextView pinSubtitle = view.findViewById(R.id.board_form_verification_subtitle_pins);
        MaterialTextView credentialSubtitle = view.findViewById(R.id.board_form_verification_subtitle_credentials);
        nameText = view.findViewById(R.id.board_form_verification_subtitle1_name_input);
        descText = view.findViewById(R.id.board_form_verification_subtitle1_description_input);
        visibilityText = view.findViewById(R.id.board_form_verification_subtitle1_visibility_input);
        boardTypeText = view.findViewById(R.id.board_form_verification_subtitle2_board_input);
        noOfPinsText = view.findViewById(R.id.board_form_verification_subtitle2_pins_input);
        usernameText = view.findViewById(R.id.board_form_verification_subtitle3_username_input);
        passwordText = view.findViewById(R.id.board_form_verification_subtitle3_password_input);
        numberPicker = view.findViewById(R.id.board_form_verification_subtitle2_number_picker);
        LinearLayout subtitle1Layout = view.findViewById(R.id.board_form_verification_subtitle_details_layout);
        LinearLayout subtitle2Layout = view.findViewById(R.id.board_form_verification_subtitle_pins_layout);
        LinearLayout subtitle3Layout = view.findViewById(R.id.board_form_verification_subtitle_credentials_layout);
        expandOrShrink(subtitle1Layout, basicSubtitle, R.drawable.ic_looks_one_black_24dp);
        expandOrShrink(subtitle2Layout, pinSubtitle, R.drawable.ic_looks_two_black_24dp);
        numberPicker.setDisallowTouch(true);
        expandOrShrink(subtitle3Layout, credentialSubtitle, R.drawable.ic_looks_3_black_24dp);
    }

    private void expandOrShrink(LinearLayout layout, MaterialTextView textView, @DrawableRes int res){
        textView.setOnClickListener(v -> {
            boolean toExpand = (layout.getVisibility() == View.GONE);
            textView.setCompoundDrawablesWithIntrinsicBounds(res,0,(toExpand?R.drawable.ic_expand_less_black_24dp:R.drawable.ic_expand_more_black_24dp),0);
            layout.setVisibility(toExpand? View.VISIBLE: View.GONE);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BoardFormViewModel boardFormViewModel = new ViewModelProvider(requireActivity()).get(BoardFormViewModel.class);
        boardFormViewModel.getBasicData().observe(getViewLifecycleOwner(), boardBasicModel -> {
            if(boardBasicModel == null) return;
            if(!TextUtils.isEmpty(boardBasicModel.getName())){
                nameText.setText(boardBasicModel.getName());
            }
            if(!TextUtils.isEmpty(boardBasicModel.getDescription())){
                descText.setText(boardBasicModel.getName());
            }
            if(boardBasicModel.getVisibility() != -1){
                visibilityText.setText(visibilityOpt[boardBasicModel.getVisibility()]);
            }
        });
        boardFormViewModel.getPinsData().observe(getViewLifecycleOwner(), boardPinsModel -> {
            if(boardPinsModel == null) return;
            if(!TextUtils.isEmpty(boardPinsModel.getBoard())){
                boardTypeText.setText(boardPinsModel.getBoard());
            }
            if(boardPinsModel.getN() > 0){
                numberPicker.setVisibility(View.VISIBLE);
                noOfPinsText.setText(String.valueOf(boardPinsModel.getN()));
            }else{
                numberPicker.setVisibility(View.GONE);
                return;
            }
            if(!TextUtils.isEmpty(boardPinsModel.getUsablePins())){
                List<Boolean> arr = new ArrayList<>();
                String pins = boardPinsModel.getUsablePins();
                for(int i = 0;i < boardPinsModel.getN();++i){
                    arr.add(pins.charAt(i+1)=='0'?Boolean.FALSE:Boolean.TRUE);
                }
                numberPicker.setNumberOfPins(boardPinsModel.getN(), arr);
            }
        });
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
        Bundle args = new Bundle();
        args.putInt(BoardFormConst.CURRENT_FRAGMENT, 4);
        getParentFragmentManager().setFragmentResult(BoardFormConst.REQUEST, args);
    }
}