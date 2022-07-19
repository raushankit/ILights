package com.raushankit.ILghts.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.board.BoardAuthUser;

public class BoardMemberAdapter extends PagingDataAdapter<BoardAuthUser, BoardMemberAdapter.BoardMemberViewHolder> {
    private static final String TAG = "BoardMemberAdapter";

    public BoardMemberAdapter() {
        super(BoardAuthUser.DIFF_UTIL);
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

    static class BoardMemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        public BoardMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.board_member_list_item_name);
        }

        public void bind(BoardAuthUser item) {
            if(item == null){ return; }
            name.setText(item.toString());
        }
    }
}
