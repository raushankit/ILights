package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.utils.LoadRetryLayout;
import com.raushankit.ILghts.utils.StringUtils;

import java.util.Map;

public class BoardSearchAdapter extends ListAdapter<BoardRoomData, BoardSearchAdapter.BoardSearchViewHolder> {

    private String descString;
    private final Map<String, Boolean> userBoards;

    public BoardSearchAdapter(@NonNull Map<String, Boolean> userBoards) {
        super(BoardRoomData.DIFF_UTIL);
        this.userBoards = userBoards;
    }

    @NonNull
    @Override
    public BoardSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        descString = parent.getContext().getString(R.string.board_list_item_details);
        return new BoardSearchAdapter.BoardSearchViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_search_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BoardSearchViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BoardSearchViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final MaterialButton requestButton;
        private final LoadRetryLayout loadRetryLayout;

        public BoardSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.board_search_list_item_title);
            description = itemView.findViewById(R.id.board_search_list_item_description);
            requestButton = itemView.findViewById(R.id.board_search_list_item_request_button);
            loadRetryLayout = new LoadRetryLayout(itemView.findViewById(R.id.board_search_list_item_loader_layout));
        }

        void bind(BoardRoomData data) {
            if(data == null){ return; }
            title.setText(data.getData().getTitle());
            description.setText(
                    String.format(descString,
                    "", data.getOwnerName(),
                    data.getOwnerEmail(),
                            StringUtils.formattedTime(data.getTime())));
            requestButton.setEnabled(!userBoards.containsKey(data.getBoardId()));
            requestButton.setText(requestButton.isEnabled()
                    ? R.string.request_access_board
                    : Boolean.TRUE.equals(userBoards.get(data.getBoardId()))
                            ? R.string.already_member
                            : R.string.already_requested
            );
        }
    }
}
