package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.List;
import java.util.stream.Collectors;

import kotlin.Triple;

public class ColorSelectorAdapter extends RecyclerView.Adapter<ColorSelectorAdapter.ColorSelectorViewHolder> {

    private int checkedIndex = -1;

    private final CallBack<Triple<Integer, Integer, String>> callBack;

    private final List<Integer> data;

    private List<Integer> colors;

    private final List<String> colorsNames;

    public ColorSelectorAdapter(@NonNull List<Integer> data, @NonNull List<String> colorNames, int checkedIndex, @NonNull CallBack<Triple<Integer, Integer, String>> callBack) {
        this.data = data;
        this.colorsNames = colorNames;
        this.checkedIndex = checkedIndex;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ColorSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(colors == null) {
            colors = data.stream()
                    .map(c -> parent.getContext().getColor(c))
                    .collect(Collectors.toList());
        }
        return new ColorSelectorViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_settings_appearance_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ColorSelectorViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ColorSelectorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final MaterialCardView cardView;
        private final View palette;
        private final TextView colorName;

        public ColorSelectorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.fragment_setting_appearance_parent);
            palette = itemView.findViewById(R.id.fragment_setting_appearance_list_item_color);
            colorName = itemView.findViewById(R.id.fragment_setting_appearance_list_item_name);
            cardView.setOnClickListener(this);
        }

        void bind(int val) {
            int color = colors.get(val);
            cardView.setStrokeColor(color);
            palette.setBackgroundColor(color);
            colorName.setText(colorsNames.get(val));
        }

        @Override
        public void onClick(View v) {
            checkedIndex = getBindingAdapterPosition();
            callBack.onClick(new Triple<>(checkedIndex, colors.get(checkedIndex), colorsNames.get(checkedIndex)));
        }
    }
}

