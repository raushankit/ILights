package com.raushankit.ILghts.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.color.MaterialColors;
import com.raushankit.ILghts.R;

public class ColoredPreferenceCategory extends PreferenceCategory {

    public ColoredPreferenceCategory(Context context) {
        super(context);
    }
    public ColoredPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ColoredPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        titleView.setTextColor(MaterialColors.getColor(holder.itemView, R.attr.baseColor));
    }

}
