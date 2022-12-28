package com.raushankit.ILghts.fragments.board;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.model.room.BoardEditableData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

import java.util.Objects;

public class BoardEditDetails extends Fragment {
    private static final String TAG = "BoardEditDetails";

    private View view;
    private BoardRoomUserData data;
    private TextInputLayout nameLayout;
    private TextInputLayout descLayout;
    private TextInputEditText nameEditText;
    private TextInputEditText descEditText;
    private MaterialButton back;
    private MaterialButton update;
    private ProgressBar progressBar;

    private String emptyErrorMessage;
    private DatabaseReference db;

    private TextWatcher nameWatcher;
    private TextWatcher descWatcher;

    public BoardEditDetails() {
        // Required empty public constructor
    }

    public static BoardEditDetails newInstance(BoardRoomUserData data) {
        BoardEditDetails fragment = new BoardEditDetails();
        Bundle args = new Bundle();
        args.putParcelable(BoardConst.BOARD_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            data = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? args.getParcelable(BoardConst.BOARD_DATA, BoardRoomUserData.class)
                    : args.getParcelable(BoardConst.BOARD_DATA);
        }else{
            Log.i(TAG, "onCreate: why here empty user data");
        }
        db = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_board_edit_details, container, false);
        TextView title = view.findViewById(R.id.fragment_board_edit_title_frag_title);
        nameLayout = view.findViewById(R.id.fragment_board_edit_name_input_layout);
        nameEditText = view.findViewById(R.id.fragment_board_edit_name_input_edit_text);
        descLayout = view.findViewById(R.id.fragment_board_edit_description_input_layout);
        descEditText = view.findViewById(R.id.fragment_board_edit_description_input_edit_text);
        progressBar = view.findViewById(R.id.fragment_board_edit_progress_bar);
        RelativeLayout navLayout = view.findViewById(R.id.fragment_board_edit_nav_button_layout);
        back = navLayout.findViewById(R.id.form_navigation_prev_button);
        update = navLayout.findViewById(R.id.form_navigation_next_button);
        emptyErrorMessage = getString(R.string.required);
        title.setText(data.getData().getTitle());
        nameEditText.setText(data.getData().getTitle());
        descEditText.setText(data.getData().getDescription());
        update.setText(R.string.update);
        initWatchers();
        back.setOnClickListener(view1 -> getParentFragmentManager().popBackStackImmediate());
        update.setOnClickListener(view1 -> {
            back.setEnabled(false);
            update.setEnabled(false);
            int check = checkData();
            if(check <= 0){
                back.setEnabled(true);
                update.setEnabled(true);
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                updateData(new BoardEditableData(Objects.requireNonNull(nameEditText.getText()).toString()
                        , Objects.requireNonNull(descEditText.getText()).toString()));
            }
        });
        return view;
    }

    private int checkData(){
        CharSequence name = nameEditText.getText();
        CharSequence desc = descEditText.getText();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)){
            if(TextUtils.isEmpty(name)){
                nameLayout.setError(emptyErrorMessage);
            }
            if(TextUtils.isEmpty(descEditText.getText())){
                descLayout.setError(emptyErrorMessage);
            }
            back.setEnabled(true);
            update.setEnabled(true);
            return -1;
        }else{
            return (TextUtils.equals(name, data.getData().getTitle()) && TextUtils.equals(desc, data.getData().getDescription()))? 0: 1;
        }
    }

    void updateData(BoardEditableData editableData){
        db.child("board_meta")
                .child(data.getBoardId())
                .child("data")
                .setValue(editableData, (error, ref) -> {
                    if(error == null){
                        progressBar.setVisibility(View.GONE);
                        Bundle args = new Bundle();
                        args.putBoolean(BoardConst.SHOW_SNACK_BAR, true);
                        args.putInt(BoardConst.SNACK_MESSAGE, R.string.details_updated_successfully);
                        getParentFragmentManager().setFragmentResult(BoardConst.REQUEST_KEY, args);
                    }else {
                        Snackbar.make(view, error.getMessage(), BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                        back.setEnabled(true);
                        update.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void initWatchers() {
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
        descWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        nameEditText.addTextChangedListener(nameWatcher);
        descEditText.addTextChangedListener(descWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        nameEditText.removeTextChangedListener(nameWatcher);
        descEditText.removeTextChangedListener(descWatcher);
    }
}