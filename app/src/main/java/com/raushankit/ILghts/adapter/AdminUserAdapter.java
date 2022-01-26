package com.raushankit.ILghts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.model.AdminUser;
import com.raushankit.ILghts.utils.callbacks.CallBack;

public class AdminUserAdapter extends ListAdapter<AdminUser, AdminUserAdapter.AdminUserViewHolder> {

    private final CallBack<AdminUser> callBack;

    public AdminUserAdapter(@NonNull CallBack<AdminUser> callBack) {
        super(AdminUser.DIFF_CALLBACK);
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_users_recycler_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

     class AdminUserViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView email;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.manage_users_recyclerview_list_name);
            email = itemView.findViewById(R.id.manage_users_recyclerview_list_email);
            itemView.setOnClickListener(view -> callBack.onClick(getCurrentList().get(getAdapterPosition())));
        }

        public void bind(AdminUser item) {
            name.setText(item.getUser().getName());
            email.setText(item.getUser().getEmail());
        }
    }
}
