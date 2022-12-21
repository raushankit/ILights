package com.raushankit.ILghts.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.List;

public class PinItemSelectorAdapter extends RecyclerView.Adapter<PinItemSelectorAdapter.PinItemSelectorViewHolder> {

    private final List<Integer> data;

    private int checkedIndex = -1;

    @ColorInt private int primaryColor;

    private final CallBack<Integer> callBack;

    public PinItemSelectorAdapter(List<Integer> data, @NonNull CallBack<Integer> callBack) {
        this.data = data;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public PinItemSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        primaryColor = MaterialColors.getColor(parent, R.attr.colorPrimary);
        return new PinItemSelectorViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dynamic_pin_selector_button_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PinItemSelectorViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PinItemSelectorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final MaterialCardView cardView;
        private final TextView button;

        public PinItemSelectorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dynamic_pin_item_selector_button_parent);
            button = itemView.findViewById(R.id.dynamic_pin_item_selector_button);
            cardView.setOnClickListener(this);
        }

        void bind(int val) {
            int value = data.get(val);
            button.setText(String.valueOf(value));
            cardView.setBackgroundTintList(val == checkedIndex? ColorStateList.valueOf(primaryColor): null);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(checkedIndex);
            checkedIndex = getBindingAdapterPosition();
            notifyItemChanged(checkedIndex);
            callBack.onClick(data.get(checkedIndex));
        }
    }
}
