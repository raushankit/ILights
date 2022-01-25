package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.utils.callbacks.CallBack;

public class EditPinAdapter extends ListAdapter<EditPinInfo, EditPinAdapter.EditPinViewHolder> {

    private final CallBack<EditPinInfo> callBack;
    private final String titleText;

    public EditPinAdapter(@NonNull String titleText, @NonNull CallBack<EditPinInfo> callBack) {
        super(EditPinInfo.DIFF_CALLBACK);
        this.titleText = titleText;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public EditPinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditPinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_pin_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EditPinViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class EditPinViewHolder extends RecyclerView.ViewHolder{

        private final TextView title;
        private final TextView name;

        public EditPinViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.add_pin_item_title);
            name = itemView.findViewById(R.id.add_pin_item_name);

            itemView.setOnClickListener(v -> {
                callBack.onClick(getCurrentList().get(getAdapterPosition()));
            });
        }

        void bind(EditPinInfo data){
            title.setText(String.format(titleText, data.getPinNumber()));
            name.setText(data.getPinInfo().getName());
        }
    }
}
