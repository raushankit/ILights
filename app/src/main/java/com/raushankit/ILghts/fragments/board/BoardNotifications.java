package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raushankit.ILghts.R;

public class BoardNotifications extends Fragment {


    public BoardNotifications() {
        // Required empty public constructor
    }

    public static BoardNotifications newInstance() {
        BoardNotifications fragment = new BoardNotifications();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_notifications, container, false);
        return view;
    }
}