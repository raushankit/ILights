package com.raushankit.ILghts.adapter;

import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BoardSearchUserAdapter extends ListAdapter<BoardSearchUserModel, BoardSearchUserAdapter.BoardSearchUserViewHolder> {
    private static final String TAG = "BoardSearchUserAdapter";
    private final Map<String, Pair<Integer, BoardSearchUserModel>> selectedUsers = new HashMap<>();
    private final CallBack<Integer> callBack;
    @ColorInt private final int selectedColor;
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
                .getColor(R.color.cards_and_dialogs_color);
        return new BoardSearchUserViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.board_search_user_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BoardSearchUserViewHolder holder, int position) {
        BoardSearchUserModel item = getItem(position);
        holder.bind(item, position == 0? item.isMember(): (!getItem(position - 1).isMember() && item.isMember()));
    }

    public Map<String, Pair<Integer, BoardSearchUserModel>> getSelectedUsers(){
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
        private final TextView sectionHeader;
        private final MaterialCheckBox checkBox;
        private final MaterialCardView cardView;
        private final LinearLayout roleLayout;
        private final MaterialButton memberButton;
        private final MaterialButton editorButton;

        public BoardSearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.board_search_user_list_parent);
            name = itemView.findViewById(R.id.board_search_user_list_item_name);
            sectionHeader = itemView.findViewById(R.id.board_search_user_section_header);
            email = itemView.findViewById(R.id.board_search_user_list_item_email);
            checkBox = itemView.findViewById(R.id.board_search_user_list_check_box);
            roleLayout = itemView.findViewById(R.id.board_search_user_list_item_role_parent_layout);
            memberButton = itemView.findViewById(R.id.board_search_user_list_item_role_member);
            editorButton = itemView.findViewById(R.id.board_search_user_list_item_role_editor);
        }

        public void bind(BoardSearchUserModel item, boolean enable) {
            if(item == null) return;
            checkBox.setOnCheckedChangeListener(null);
            cardView.setOnClickListener(null);
            sectionHeader.setVisibility(enable? View.VISIBLE: View.GONE);
            boolean selected = selectedUsers.containsKey(item.getUserId());
            if(selected){
                selectedUsers.computeIfPresent(item.getUserId(), (s, it) -> {
                    if(it.first == 1) {
                        memberButton.setEnabled(false);
                        editorButton.setEnabled(true);
                    } else {
                        memberButton.setEnabled(true);
                        editorButton.setEnabled(false);
                    }
                    return it;
                });
            } else {
                memberButton.setEnabled(true);
                editorButton.setEnabled(true);
            }
            cardView.setBackgroundColor(selected? selectedColor: cardBackgroundColor);
            checkBox.setChecked(selected);
            name.setText(StringUtils.capitalize(item.getName()));
            email.setText(item.getEmail());
            if(!item.isMember()){
                checkBox.setEnabled(true);
                checkBox.setOnCheckedChangeListener(this);
                cardView.setOnClickListener(this);
                roleLayout.setVisibility(View.VISIBLE);
            }else{
                roleLayout.setVisibility(View.GONE);
                checkBox.setEnabled(false);
                checkBox.setChecked(true);
            }
            memberButton.setOnClickListener(this);
            editorButton.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            BoardSearchUserModel model = getItem(getBindingAdapterPosition());
            cardView.setBackgroundColor(b? selectedColor: cardBackgroundColor);
            if(b){
                selectedUsers.put(model.getUserId(), Pair.create(1, model));
                memberButton.setEnabled(false);
                editorButton.setEnabled(true);
            }else{
                selectedUsers.remove(model.getUserId());
                memberButton.setEnabled(true);
                editorButton.setEnabled(true);
            }
            callBack.onClick(selectedUsers.size());
        }

        @Override
        public void onClick(View view) {
            BoardSearchUserModel model = getItem(getBindingAdapterPosition());
            if(Arrays.asList(memberButton.getId(), editorButton.getId()).contains(view.getId())) {
                if(memberButton.getId() == view.getId()) {
                    selectedUsers.put(model.getUserId(), Pair.create(1, model));
                } else {
                    selectedUsers.put(model.getUserId(), Pair.create(2, model));
                }
                cardView.setBackgroundColor(selectedColor);
            } else {
                if(selectedUsers.isEmpty()){ return; }
                boolean b = !checkBox.isChecked();
                cardView.setBackgroundColor(b? selectedColor: cardBackgroundColor);
                if(b){
                    selectedUsers.put(model.getUserId(), Pair.create(1, model));
                }else{
                    selectedUsers.remove(model.getUserId());
                }
            }
            notifyItemChanged(getBindingAdapterPosition());
            callBack.onClick(selectedUsers.size());
        }
    }
}
