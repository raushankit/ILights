package com.raushankit.ILghts.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.HashMap;
import java.util.Map;

public class BoardSearchUserAdapter extends ListAdapter<BoardSearchUserModel, BoardSearchUserAdapter.BoardSearchUserViewHolder> {
    private static final String TAG = "BoardSearchUserAdapter";
    private final Map<String, BoardSearchUserModel> selectedUsers = new HashMap<>();
    private final CallBack<Integer> callBack;
    @ColorInt private int selectedColor;
    @ColorInt private int cardBackgroundColor;

    public BoardSearchUserAdapter(@ColorInt int selectedColor, @NonNull CallBack<Integer> callBack) {
        super(BoardSearchUserModel.DIFF_CALLBACK);
        this.selectedColor = selectedColor;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public BoardSearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        cardBackgroundColor = parent.getContext()
                .getColor(R.color.list_item_background);
        return new BoardSearchUserViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_search_user_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BoardSearchUserViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public Map<String, BoardSearchUserModel> getSelectedUsers(){
        return selectedUsers;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelection(){
        if(selectedUsers.isEmpty()){ return; }
        selectedUsers.clear();
        notifyDataSetChanged();
    }

    class BoardSearchUserViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private final TextView name;
        private final TextView email;
        private final MaterialCheckBox checkBox;
        private final MaterialCardView cardView;

        public BoardSearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.board_search_user_list_parent);
            name = itemView.findViewById(R.id.board_search_user_list_item_name);
            email = itemView.findViewById(R.id.board_search_user_list_item_email);
            checkBox = itemView.findViewById(R.id.board_search_user_list_check_box);
        }

        public void bind(BoardSearchUserModel item) {
            if(item == null) return;
            checkBox.setOnCheckedChangeListener(null);
            boolean selected = selectedUsers.containsKey(item.getUserId());
            cardView.setBackgroundColor(selected? selectedColor: cardBackgroundColor);
            checkBox.setChecked(selected);
            name.setText(item.getName());
            email.setText(item.getEmail());
            checkBox.setOnCheckedChangeListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            BoardSearchUserModel model = getItem(getBindingAdapterPosition());
            cardView.setBackgroundColor(b? selectedColor: cardBackgroundColor);
            if(b){
                selectedUsers.put(model.getUserId(), model);
            }else{
                selectedUsers.remove(model.getUserId());
            }
            callBack.onClick(selectedUsers.size());
        }

        @Override
        public void onClick(View view) {
            if(selectedUsers.isEmpty()){ return; }
            BoardSearchUserModel model = getItem(getBindingAdapterPosition());
            boolean b = !checkBox.isChecked();
            cardView.setBackgroundColor(b? selectedColor: cardBackgroundColor);
            if(b){
                selectedUsers.put(model.getUserId(), model);
            }else{
                selectedUsers.remove(model.getUserId());
            }
            notifyItemChanged(getBindingAdapterPosition());
            callBack.onClick(selectedUsers.size());
        }
    }
}
