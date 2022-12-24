package com.raushankit.ILghts.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

public class PinListAdapter extends ListAdapter<PinListData, PinListAdapter.PinListViewHolder> {

    private static final String TAG = "PinListAdapter";

    private final String detailsString;
    private final CallBack<Pair<WhichButton, PinListData>> callBack;
    private final Role role;

    public PinListAdapter(@NonNull String detailsString,
                          @NonNull CallBack<Pair<WhichButton, PinListData>> callBack) {
        super(PinListData.DIFF_CALLBACK);
        this.detailsString = detailsString;
        this.callBack = callBack;
        this.role = new Role(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRole(int level) {
        role.setAccessLevel(level);
        notifyDataSetChanged();
    }

    public Role getRole() {
        return role;
    }

    @NonNull
    @Override
    public PinListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PinListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pin_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PinListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PinListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView details;
        private final MaterialSwitch _switch;
        private final MaterialCardView cardView;

        public PinListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pin_item_title);
            details = itemView.findViewById(R.id.pin_item_details);
            _switch = itemView.findViewById(R.id.pin_item_switch);
            cardView = itemView.findViewById(R.id.pin_item_card_view);
            MaterialButton editButton = itemView.findViewById(R.id.pin_item_edit_button);
            MaterialButton deleteButton = itemView.findViewById(R.id.pin_item_delete_button);
            editButton.setVisibility(role.getAccessLevel() >= 2? View.VISIBLE: View.GONE);
            deleteButton.setVisibility(role.getAccessLevel() >= 2? View.VISIBLE: View.GONE);
            _switch.setOnClickListener(this);
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        void bind(PinListData data) {
            name.setText(data.getPinName());
            _switch.setChecked(data.isStatus());
            cardView.setStrokeColor(data.isStatus()? Color.GREEN: Color.TRANSPARENT);
            details.setText(String.format(detailsString, data.getPinNumber(), (data.isStatus() ? "ON" : "OFF"), (data.isYou() ? "You" : StringUtils.capitalize(data.getChangedBy())), StringUtils.formattedTime(data.getChangedAt()), data.getPinDescription()));
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.pin_item_switch) {
                callBack.onClick(Pair.create(WhichButton.SWITCH, getCurrentList().get(getBindingAdapterPosition())));
            } else if (id == R.id.pin_item_edit_button) {
                callBack.onClick(Pair.create(WhichButton.EDIT, getCurrentList().get(getBindingAdapterPosition())));
            } else if (id == R.id.pin_item_delete_button) {
                callBack.onClick(Pair.create(WhichButton.DELETE, getCurrentList().get(getBindingAdapterPosition())));
            } else {
                Log.w(TAG, "onClick: unknown id = " + id);
            }
        }
    }

    public enum WhichButton {
        EDIT, DELETE, SWITCH
    }
}
