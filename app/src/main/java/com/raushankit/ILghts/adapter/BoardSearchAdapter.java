package com.raushankit.ILghts.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.utils.NoFilterArrayAdapter;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.Collections;
import java.util.Map;

import kotlin.Triple;

public class BoardSearchAdapter extends ListAdapter<BoardRoomData, BoardSearchAdapter.BoardSearchViewHolder> {

    private String descString;
    private Map<String, Boolean> userBoards;
    private String[] roles;
    private CallBack<Boolean> trigger;
    private final CallBack<Triple<Integer, Integer, BoardRoomData>> requestCallBack;

    public BoardSearchAdapter(@NonNull CallBack<Triple<Integer, Integer, BoardRoomData>> requestCallBack) {
        super(BoardRoomData.DIFF_UTIL);
        userBoards = Collections.emptyMap();
        this.requestCallBack = requestCallBack;
    }

    public void addTrigger(@NonNull CallBack<Boolean> trigger) {
        this.trigger = trigger;
    }

    public void setUserBoards(Map<String, Boolean> userBoards) {
        this.userBoards = userBoards;
    }

    @NonNull
    @Override
    public BoardSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        descString = parent.getContext().getString(R.string.board_list_item_details);
        roles = parent.getContext().getResources().getStringArray(R.array.request_access_roles);
        return new BoardSearchAdapter.BoardSearchViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_search_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BoardSearchViewHolder holder, int position) {
        holder.bind(getItem(position));
        Log.e("HELLO", "onBindViewHolder: position = " + position + " count = " + getItemCount());
        if(trigger != null && position == getItemCount() - 1) {
            trigger.onClick(true);
        }
    }

    public void updateUserBoards(String boardId, boolean value) {
        if(userBoards != null) userBoards.put(boardId, value);
    }

    class BoardSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final MaterialCardView cardView;
        private final TextView title;
        private final TextView description;
        private final MaterialButton requestButton;
        private final TextInputLayout roleLayout;
        private final AutoCompleteTextView rolesEditText;

        public BoardSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.board_search_list_card);
            title = itemView.findViewById(R.id.board_search_list_item_title);
            description = itemView.findViewById(R.id.board_search_list_item_description);
            requestButton = itemView.findViewById(R.id.board_search_list_item_request_button);
            requestButton.setOnClickListener(this);
            rolesEditText = itemView.findViewById(R.id.board_search_list_item_edit_text);
            roleLayout = itemView.findViewById(R.id.board_search_list_item_input_layout);
            NoFilterArrayAdapter<String> rolesAdapter = new NoFilterArrayAdapter<>(itemView.getContext(), R.layout.dropdown_item, roles);
            rolesEditText.setAdapter(rolesAdapter);
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    roleLayout.setError(null);
                }
            };
            rolesEditText.addTextChangedListener(watcher);
        }

        void bind(BoardRoomData data) {
            if(data == null){ return; }
            title.setText(data.getData().getTitle());
            description.setText(
                    String.format(descString,
                    data.getData().getDescription(), data.getOwnerName(),
                    data.getOwnerEmail(),
                            StringUtils.formattedTime(data.getTime())));
            requestButton.setEnabled(!userBoards.containsKey(data.getBoardId()));
            roleLayout.setVisibility(userBoards.containsKey(data.getBoardId())? View.GONE: View.VISIBLE);
            cardView.setChecked(userBoards.containsKey(data.getBoardId()));
            requestButton.setText(requestButton.isEnabled()
                    ? R.string.request_access_board
                    : Boolean.TRUE.equals(userBoards.get(data.getBoardId()))
                            ? R.string.already_member
                            : R.string.already_requested
            );
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.board_search_list_item_request_button) {
                if(TextUtils.isEmpty(rolesEditText.getText())) {
                    roleLayout.setError("select a role");
                } else {
                    requestCallBack.onClick(new Triple<>(getBindingAdapterPosition(),
                            "user".equalsIgnoreCase(rolesEditText.getText().toString()) ? 1: 2,
                            getCurrentList().get(getBindingAdapterPosition())));
                }
            }
        }
    }
}
