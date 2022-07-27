package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raushankit.ILghts.R;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private View view;
    private String uid;


    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(@NonNull String uid) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString("user_id", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        uid = args.getString("user_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

}