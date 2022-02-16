package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

public class PinListAdapter extends ListAdapter<PinListData, PinListAdapter.PinListViewHolder> {

    private final String detailsString;
    private final CallBack<PinListData> callBack;

    public PinListAdapter(@NonNull String detailsString, @NonNull CallBack<PinListData> callBack) {
        super(PinListData.DIFF_CALLBACK);
        this.detailsString = detailsString;
        this.callBack = callBack;
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
        private final SwitchCompat _switch;

        public PinListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pin_item_title);
            details = itemView.findViewById(R.id.pin_item_details);
            _switch = itemView.findViewById(R.id.pin_item_switch);
            _switch.setOnClickListener(this);
        }

        void bind(PinListData data) {
            name.setText(data.getPinName());
            _switch.setChecked(data.isStatus());
            details.setText(String.format(detailsString, data.getPinNumber(), (data.isStatus() ? "ON" : "OFF"), (data.isYou() ? "You" : StringUtils.capitalize(data.getChangedBy())), StringUtils.formattedTime(data.getChangedAt())));
        }

        @Override
        public void onClick(View view) {
            callBack.onClick(getCurrentList().get(getAdapterPosition()));
        }
    }
}
