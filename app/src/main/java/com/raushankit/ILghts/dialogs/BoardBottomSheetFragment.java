package com.raushankit.ILghts.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.room.BoardRoomUserData;

public class BoardBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private static final String TAG = "BoardBottomSheetFragment";
    public static final String DATA_PARAM_1 = "data";

    private BoardRoomUserData data;
    private WhichButtonCLickedListener listener;

    public static BoardBottomSheetFragment newInstance(BoardRoomUserData data){
        BoardBottomSheetFragment fragment = new BoardBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA_PARAM_1, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_bottom_sheet_board_options, container, false);
        getData();
        init(view);
        return view;
    }

    private void init(View view) {
        TextView title = view.findViewById(R.id.frag_bottom_sheet_board_options_header);
        TextView userId = view.findViewById(R.id.frag_bottom_sheet_board_options_uid);
        MaterialButton copyIdBtn = view.findViewById(R.id.frag_bottom_sheet_board_options_copy_id_button);
        MaterialButton editDetails = view.findViewById(R.id.frag_bottom_sheet_board_options_edit_details_button);
        MaterialButton showCredBtn = view.findViewById(R.id.frag_bottom_sheet_board_options_see_cred_button);
        MaterialButton addMembers = view.findViewById(R.id.frag_bottom_sheet_board_options_add_members_button);
        MaterialButton editMembers = view.findViewById(R.id.frag_bottom_sheet_board_options_edit_members_button);
        MaterialButton goToBoardBtn = view.findViewById(R.id.frag_bottom_sheet_board_options_go_to_board_button);
        MaterialButton approveReqButton = view.findViewById(R.id.frag_bottom_sheet_board_options_approve_request_button);
        MaterialButton deleteBoardBtn = view.findViewById(R.id.frag_bottom_sheet_board_options_delete_board_button);
        MaterialButton leaveBoardBtn = view.findViewById(R.id.frag_bottom_sheet_board_options_leave_board_button);
        int level = data.getAccessLevel();
        copyIdBtn.setVisibility(level >= 2? View.VISIBLE: View.GONE);
        editDetails.setVisibility(level >= 2? View.VISIBLE: View.GONE);
        showCredBtn.setVisibility(level >= 3? View.VISIBLE: View.GONE);
        addMembers.setVisibility(level >= 3? View.VISIBLE: View.GONE);
        editMembers.setVisibility(level >= 3? View.VISIBLE: View.GONE);
        goToBoardBtn.setVisibility(level >= 0? View.VISIBLE: View.GONE);
        approveReqButton.setVisibility(level >= 3? View.VISIBLE: View.GONE);
        deleteBoardBtn.setVisibility(level >= 3? View.VISIBLE: View.GONE);
        leaveBoardBtn.setVisibility(level <= 2? View.VISIBLE: View.GONE);

        userId.setText(getString(R.string.frag_bottom_sheet_board_options_uid_text, data.getBoardId()));
        title.setText(data.getData().getTitle());
        approveReqButton.setText(data.getAccessLevel() == 1? R.string.request_higher_access: R.string.approve_requests);

        copyIdBtn.setOnClickListener(this);
        editDetails.setOnClickListener(this);
        showCredBtn.setOnClickListener(this);
        addMembers.setOnClickListener(this);
        editMembers.setOnClickListener(this);
        goToBoardBtn.setOnClickListener(this);
        approveReqButton.setOnClickListener(this);
        deleteBoardBtn.setOnClickListener(this);
        leaveBoardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(listener == null) return;
        if(id == R.id.frag_bottom_sheet_board_options_copy_id_button){
            listener.onCLick(WhichButton.COPY_ID);
        } else if(id == R.id.frag_bottom_sheet_board_options_edit_details_button){
            listener.onCLick(WhichButton.EDIT_DETAILS);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_see_cred_button){
            listener.onCLick(WhichButton.SHOW_CREDENTIALS);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_add_members_button){
            listener.onCLick(WhichButton.ADD_MEMBERS);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_edit_members_button){
            listener.onCLick(WhichButton.EDIT_MEMBERS);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_go_to_board_button){
            listener.onCLick(WhichButton.GO_TO_BOARD);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_approve_request_button){
            listener.onCLick(data.getAccessLevel() == 1? WhichButton.GET_HIGHER_ACCESS: WhichButton.APPROVE_REQUESTS);
        }
        else if(id == R.id.frag_bottom_sheet_board_options_delete_board_button){
            listener.onCLick(WhichButton.DELETE_BOARD);
        } else if(id == R.id.frag_bottom_sheet_board_options_leave_board_button){
            listener.onCLick(WhichButton.LEAVE_BOARD);
        } else{
            Log.i(TAG, "onClick: unknown type");
        }
        dismiss();
    }

    @Override
    public int getTheme() {
        return R.style.RoundBottomSheetDialog;
    }

    public void addOnClickListener(@NonNull WhichButtonCLickedListener listener){
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }

    private void getData(){
        Bundle args = getArguments();
        if(args == null){
            if(listener != null) listener.onCLick(WhichButton.NO_ARGS);
            dismiss();
            return;
        }
        data = args.getParcelable(DATA_PARAM_1);
    }

    public interface WhichButtonCLickedListener {
        void onCLick(WhichButton whichButton);
    }

    public enum WhichButton {
        COPY_ID, EDIT_DETAILS, SHOW_CREDENTIALS,ADD_MEMBERS, EDIT_MEMBERS, GO_TO_BOARD, APPROVE_REQUESTS, GET_HIGHER_ACCESS, DELETE_BOARD, LEAVE_BOARD, NO_ARGS
    }
}
