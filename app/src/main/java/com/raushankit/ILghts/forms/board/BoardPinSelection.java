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
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.utils.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardPinSelection extends Fragment {
    private static final String TAG = "BoardPinSelection";

    private TextInputLayout pickBoardLayout;
    private MaterialAutoCompleteTextView pickBoardSpinner;
    private TextInputLayout pinNumberLayout;
    private TextInputEditText pinNumber;
    private final List<String> boardNames = new ArrayList<>();
    private TextWatcher pickBoardWatcher;
    private TextWatcher pinNumberWatcher;
    private String errorString;
    private String pinErrorString;

    private NumberPicker numberPicker;

    public BoardPinSelection() {
        // Required empty public constructor
    }

    public static BoardPinSelection newInstance() {
        BoardPinSelection fragment = new BoardPinSelection();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_pin_selection, container, false);
        pickBoardLayout = view.findViewById(R.id.board_form_pin_selection_input_layout);
        pickBoardSpinner = view.findViewById(R.id.board_form_pin_selection_edit_text);
        pinNumberLayout = view.findViewById(R.id.board_form_pin_selection_number_input_layout);
        pinNumber = view.findViewById(R.id.board_form_pin_selection_number_edit_text);
        numberPicker = view.findViewById(R.id.board_form_pin_selection_ui);
        pinNumberLayout.setVisibility(View.GONE);
        numberPicker.setVisibility(View.GONE);
        init(view);
        return view;
    }

    private void init(View view) {
        RelativeLayout navLayout = view.findViewById(R.id.board_form_pin_selection_nav_button_layout);
        MaterialButton prevButton = navLayout.findViewById(R.id.form_navigation_prev_button);
        MaterialButton nextButton = navLayout.findViewById(R.id.form_navigation_next_button);
        String []boards = getResources().getStringArray(R.array.board_form_pin_selection_boards);
        boardNames.addAll(Arrays.asList(boards));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, boards);
        pickBoardSpinner.setAdapter(adapter);
        pickBoardSpinner.setThreshold(100);
        errorString = getString(R.string.required);
        pinErrorString = getString(R.string.board_form_pin_selection_choose_pins_error_text);

        pickBoardWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pickBoardLayout.setError(null);
                onBoardTextChange(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        pinNumberWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinNumberLayout.setError(null);
                if(!TextUtils.isEmpty(charSequence)){
                    numberPicker.setNumberOfPins(Integer.parseInt(String.valueOf(charSequence)), setPins());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        numberPicker.addOnSquareClickedListener(whichSquare -> pinNumberLayout.setError(null));

        prevButton.setOnClickListener(v -> {
            //TODO: go to previous frame
            getParentFragmentManager().popBackStackImmediate();
        });

        nextButton.setOnClickListener(v -> {
            if(checkValidity()){
                Log.w(TAG, "init: everything is fine");
            }
        });
    }

    private boolean checkValidity(){
        if(TextUtils.isEmpty(pickBoardSpinner.getText())){
            pickBoardLayout.setError(errorString);
            return false;
        }
        boolean flag = numberPicker.isAnyPinSelected();
        if(!flag) pinNumberLayout.setError(pinErrorString);
        return flag;
    }

    private void onBoardTextChange(CharSequence charSequence){
        if(TextUtils.equals(charSequence, boardNames.get(0))){
            pinNumber.setText(R.string.esp8266_total_pin);
            pinNumber.setInputType(InputType.TYPE_NULL);
            pinNumberLayout.setVisibility(View.VISIBLE);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setDisallowTouch(true);
        }
        else if(TextUtils.equals(charSequence, boardNames.get(1))){
            pinNumber.setText(R.string.esp32_total_pin);
            pinNumber.setInputType(InputType.TYPE_NULL);
            pinNumberLayout.setVisibility(View.VISIBLE);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setDisallowTouch(true);
        }else if(TextUtils.equals(charSequence, boardNames.get(2))){
            pinNumber.setText("1");
            pinNumber.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            pinNumberLayout.setVisibility(View.VISIBLE);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setDisallowTouch(false);
        }else{
            Log.w(TAG, "onTextChanged: unknown board option");
        }
    }

    private List<Boolean> setPins(){
        if(TextUtils.equals(pickBoardSpinner.getText(), boardNames.get(0))){
            Boolean []active = new Boolean[Integer.parseInt(String.valueOf(pinNumber.getText()))];
            for(int i: new int[]{1, 3, 4, 9, 11, 12, 13, 14, 15, 16}){
                active[i] = Boolean.TRUE;
            }
            for(int i = 0;i < active.length;++i){
                if(active[i] == null) active[i] = Boolean.FALSE;
            }
            return Arrays.asList(active);
        } else if(TextUtils.equals(pickBoardSpinner.getText(), boardNames.get(1))){
            Boolean []active = new Boolean[Integer.parseInt(String.valueOf(pinNumber.getText()))];
            for(int i: new int[]{1, 3, 4}){
                active[i] = Boolean.TRUE;
            }
            for(int i = 11;i < 33;++i){
                active[i] = Boolean.TRUE;
            }
            for(int i = 0;i < active.length;++i){
                if(active[i] == null) active[i] = Boolean.FALSE;
            }
            return Arrays.asList(active);
        }else {
            Log.w(TAG, "setPins: custom board option type");
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        pickBoardSpinner.addTextChangedListener(pickBoardWatcher);
        pinNumber.addTextChangedListener(pinNumberWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        pickBoardSpinner.removeTextChangedListener(pickBoardWatcher);
        pinNumber.removeTextChangedListener(pinNumberWatcher);
    }

}