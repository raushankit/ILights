package com.raushankit.ILghts.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.utils.StringUtils;

public class BoardMemberAdapter extends PagingDataAdapter<BoardAuthUser, BoardMemberAdapter.BoardMemberViewHolder> {
    private static final String TAG = "BoardMemberAdapter";
    private static final String[] userTypes = {"", "USER", "EDITOR", "OWNER"};
    private WhichButtonClickedListener listener;
    public BoardMemberAdapter() {
        super(BoardAuthUser.DIFF_UTIL);
        Log.i(TAG, "BoardMemberAdapter() called");
    }

    public void addListener(@NonNull WhichButtonClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BoardMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoardMemberViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_member_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardMemberViewHolder holder, int position) {
        BoardAuthUser item = getItem(position);
        if(item != null){
            holder.bind(item);
        }
    }

    class BoardMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView name;
        private final TextView email;
        private final TextView userType;
        private final RatingBar ratingBar;
        private final MaterialButton deleteButton;
        private final MaterialButton promoteButton;
        public BoardMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.board_member_list_item_name);
            email = itemView.findViewById(R.id.board_member_list_item_email);
            userType = itemView.findViewById(R.id.board_member_list_item_level_text);
            ratingBar = itemView.findViewById(R.id.board_member_list_item_rating_bar);
            deleteButton = itemView.findViewById(R.id.board_member_list_item_delete_button);
            promoteButton = itemView.findViewById(R.id.board_member_list_item_promote_button);
            promoteButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        public void bind(BoardAuthUser item) {
            if(item == null){ return; }
            name.setText(StringUtils.capitalize(item.getName()));
            email.setText(item.getEmail());
            userType.setText(userTypes[Math.min(3, Math.max(0, item.getLevel()))]);
            deleteButton.setVisibility(item.getLevel() < 3? View.VISIBLE: View.GONE);
            promoteButton.setVisibility(item.getLevel() == 3? View.GONE: View.VISIBLE);
            promoteButton.setText(item.getLevel() == 1? R.string.promote_to_editor: R.string.demote_to_user);
            promoteButton.setIconResource(item.getLevel() == 1? R.drawable.ic_keyboard_double_arrow_up: R.drawable.ic_keyboard_double_arrow_down);
            ratingBar.setRating(item.getLevel());
        }

        @Override
        public void onClick(View view) {
            if(listener == null) { return; }
            Log.w(TAG, "onClick: " + getItem(getBindingAdapterPosition()));
            int id = view.getId();
            if(id == R.id.board_member_list_item_delete_button){
                listener.onClick(WhichButton.DELETE, getItem(getBindingAdapterPosition()));
            }else if(id == R.id.board_member_list_item_promote_button){
                listener.onClick(WhichButton.PROMOTE, getItem(getBindingAdapterPosition()));
            }else{
                Log.i(TAG, "onClick: unknown button clicked");
            }
        }
    }

    public interface WhichButtonClickedListener {
        void onClick(WhichButton button, BoardAuthUser user);
    }

    public enum WhichButton {
        DELETE, PROMOTE
    }
}
