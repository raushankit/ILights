package com.raushankit.ILghts.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.utils.StringUtils;

public class BoardUserItemAdapter extends ListAdapter<BoardRoomUserData, BoardUserItemAdapter.BoardUserItemViewHolder> {

    private static final String TAG = "BoardUserItemAdapter";
    private String detailsString;
    private String boardIdString;
    private final String []accessArray = {"", "user", "editor", "owner"};

    public BoardUserItemAdapter() {
        super(BoardRoomUserData.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public BoardUserItemAdapter.BoardUserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        detailsString = parent.getContext().getString(R.string.board_list_item_details);
        boardIdString = parent.getContext().getString(R.string.board_list_item_board_id);
        return new BoardUserItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardUserItemAdapter.BoardUserItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BoardUserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final TextView title;
        private final TextView tagVisibility;
        private final TextView tagUserLevel;
        private final TextView uidText;
        private final TextView description;
        private final ImageView copyIdBtn;
        private final ImageView goToBoard;
        private final FrameLayout showOptionsBtn;

        public BoardUserItemViewHolder(@NonNull View itemView) {
            super(itemView);
            MaterialCardView cardView = itemView.findViewById(R.id.board_list_top_card_view);
            title = itemView.findViewById(R.id.board_list_item_title);
            description = itemView.findViewById(R.id.board_list_item_description);
            tagVisibility = itemView.findViewById(R.id.board_list_item_visibility_tag_text);
            tagUserLevel = itemView.findViewById(R.id.board_list_item_access_tag_text);
            copyIdBtn = itemView.findViewById(R.id.board_list_item_copy_button);
            goToBoard = itemView.findViewById(R.id.board_list_item_switch_button);
            showOptionsBtn = itemView.findViewById(R.id.board_list_item_ellipsis_menu);
            TextView statusTag = itemView.findViewById(R.id.board_list_item_status_tag_text);
            statusTag.setVisibility(View.GONE);
            tagVisibility.setVisibility(View.VISIBLE);
            tagUserLevel.setVisibility(View.VISIBLE);
            uidText = itemView.findViewById(R.id.board_list_item_uid_text);
            Log.e(TAG, "BoardUserItemViewHolder: boardUserItem constructor");
            cardView.setOnClickListener(this);
            copyIdBtn.setOnClickListener(this);
            goToBoard.setOnClickListener(this);
            showOptionsBtn.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        void bind(@NonNull BoardRoomUserData item){
            title.setText(item.getData().getTitle());
            description.setText(String.format(detailsString, item.getData().getDescription(),
                    item.getOwnerName(), StringUtils.formattedTime(item.getTime())));
            tagVisibility.setText(item.getVisibility());
            tagUserLevel.setText(accessArray[item.getAccessLevel()]);
            uidText.setText(String.format(boardIdString, item.getBoardId()));
            copyIdBtn.setVisibility(item.getAccessLevel() >= 3?View.VISIBLE:View.GONE);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.board_list_top_card_view){

            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(view.getId() == R.id.board_list_top_card_view){
                Log.d("cardview","cardView is checked: " + getCurrentList().get(getBindingAdapterPosition()));
            }
            return true;
        }
    }
}
