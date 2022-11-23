package com.raushankit.ILghts.adapter;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
    private int activeColor;
    private int inactiveColor;
    private final WhichIconClickedListener listener;
    private final String []accessArray = {"", "user", "editor", "owner"};

    public BoardUserItemAdapter(@NonNull WhichIconClickedListener listener) {
        super(BoardRoomUserData.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BoardUserItemAdapter.BoardUserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        detailsString = parent.getContext().getString(R.string.board_list_item_details);
        boardIdString = parent.getContext().getString(R.string.board_list_item_board_id);
        activeColor = parent.getContext().getColor(R.color.scarlet_red);
        inactiveColor = parent.getContext().getColor(R.color.board_list_item_title_text);
        return new BoardUserItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardUserItemAdapter.BoardUserItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BoardUserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final ImageView likeButton;
        private final TextView title;
        private final TextView tagVisibility;
        private final TextView tagUserLevel;
        private final TextView uidText;
        private final TextView description;
        private final ImageView copyIdBtn;

        public BoardUserItemViewHolder(@NonNull View itemView) {
            super(itemView);
            MaterialCardView cardView = itemView.findViewById(R.id.board_list_top_card_view);
            likeButton = itemView.findViewById(R.id.board_list_item_like_button);
            title = itemView.findViewById(R.id.board_list_item_title);
            description = itemView.findViewById(R.id.board_list_item_description);
            tagVisibility = itemView.findViewById(R.id.board_list_item_visibility_tag_text);
            tagUserLevel = itemView.findViewById(R.id.board_list_item_access_tag_text);
            copyIdBtn = itemView.findViewById(R.id.board_list_item_copy_button);
            ImageView goToBoard = itemView.findViewById(R.id.board_list_item_switch_button);
            FrameLayout showOptionsBtn = itemView.findViewById(R.id.board_list_item_ellipsis_menu);
            TextView statusTag = itemView.findViewById(R.id.board_list_item_status_tag_text);
            statusTag.setVisibility(View.GONE);
            tagVisibility.setVisibility(View.VISIBLE);
            tagUserLevel.setVisibility(View.VISIBLE);
            uidText = itemView.findViewById(R.id.board_list_item_uid_text);
            likeButton.setOnClickListener(this);
            cardView.setOnClickListener(this);
            copyIdBtn.setOnClickListener(this);
            goToBoard.setOnClickListener(this);
            showOptionsBtn.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        void bind(@NonNull BoardRoomUserData item){
            likeButton.setColorFilter(item.isFav()? activeColor: inactiveColor, PorterDuff.Mode.SRC_ATOP);
            title.setText(item.getData().getTitle());
            description.setText(String.format(detailsString, item.getData().getDescription(),
                    StringUtils.capitalize(item.getOwnerName()),
                    item.getOwnerEmail(),
                    StringUtils.formattedTime(item.getTime())));
            tagVisibility.setText(item.getVisibility());
            tagUserLevel.setText(accessArray[Math.min(3, Math.max(0, item.getAccessLevel()))]);
            uidText.setText(String.format(boardIdString, item.getBoardId()));
            copyIdBtn.setVisibility(item.getAccessLevel() >= 3 ?View.VISIBLE :View.GONE);
        }

        @Override
        public void onClick(View view) {
            BoardRoomUserData item = getCurrentList().get(getBindingAdapterPosition());
            if(view.getId() == R.id.board_list_top_card_view){
                listener.onButtonCLick(ClickType.GO_TO_BOARD, item);
            }else if(view.getId() == R.id.board_list_item_copy_button){
                listener.onButtonCLick(ClickType.COPY_ID, item);
            }else if(view.getId() == R.id.board_list_item_switch_button){
                listener.onButtonCLick(ClickType.GO_TO_BOARD, item);
            }else if(view.getId() == R.id.board_list_item_ellipsis_menu){
                listener.onButtonCLick(ClickType.SHOW_OPTIONS, item);
            }else if(view.getId() == R.id.board_list_item_like_button) {
                listener.onButtonCLick(ClickType.LIKE_BOARD, item);
            }else{
                Log.i(TAG, "onClick: unknown");
            }
        }

        @Override
        public boolean onLongClick(View view) {
            BoardRoomUserData item = getCurrentList().get(getBindingAdapterPosition());
            if(view.getId() == R.id.board_list_top_card_view){
                listener.onButtonCLick(ClickType.SHOW_OPTIONS, item);
            }else{
                Log.i(TAG, "onLongClick: unknown");
            }
            return true;
        }
    }

    public enum ClickType {
        COPY_ID, GO_TO_BOARD, SHOW_OPTIONS, LIKE_BOARD
    }

    public interface WhichIconClickedListener {
        void onButtonCLick(ClickType type, BoardRoomUserData data);
    }
}
