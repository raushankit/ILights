package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.R;

import java.util.List;

public class PinItemSelectorAdapter extends RecyclerView.Adapter<PinItemSelectorAdapter.PinItemSelectorViewHolder> {

    private final List<Integer> data;

    public PinItemSelectorAdapter(List<Integer> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PinItemSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PinItemSelectorViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dynamic_pin_selector_button_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PinItemSelectorViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class PinItemSelectorViewHolder extends RecyclerView.ViewHolder {

        private final MaterialButton button;

        public PinItemSelectorViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.dynamic_pin_item_selector_button);
        }

        void bind(int value) {
            button.setText(String.valueOf(value));
        }
    }
}
