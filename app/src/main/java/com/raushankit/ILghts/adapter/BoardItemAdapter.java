package com.raushankit.ILghts.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.BoardItem;

public class BoardItemAdapter extends ListAdapter<BoardItem, BoardItemAdapter.BoardItemViewHolder> {

    private static final String TAG = "BoardItemAdapter";
    private String detailsString;

    public BoardItemAdapter() {
        super(BoardItem.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public BoardItemAdapter.BoardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        detailsString = parent.getContext().getString(R.string.board_list_item_details);
        return new BoardItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardItemAdapter.BoardItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BoardItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final TextView title;
        private final TextView description;

        public BoardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            MaterialCardView cardView = itemView.findViewById(R.id.board_list_top_card_view);
            title = itemView.findViewById(R.id.board_list_item_title);
            description = itemView.findViewById(R.id.board_list_item_description);
            Log.e(TAG, "BoardItemViewHolder: boardItem constructor");
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        void bind(@NonNull BoardItem item){
            title.setText(item.getTitle());
            description.setText(String.format(detailsString, "This is used for water pump", "Ankit Raushan", "03-07-2022 12:157AM"));
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.board_list_top_card_view){

            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(view.getId() == R.id.board_list_top_card_view){
                Log.d("cardview","cardView is checked: " + getCurrentList().get(getAdapterPosition()));
            }
            return true;
        }
    }
}
