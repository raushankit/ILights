package com.raushankit.ILghts.forms.board;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raushankit.ILghts.R;

public class BoardPinSelection extends Fragment {


    public BoardPinSelection() {
        // Required empty public constructor
    }

    public static BoardPinSelection newInstance() {
        BoardPinSelection fragment = new BoardPinSelection();
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
        View view = inflater.inflate(R.layout.fragment_board_pin_selection, container, false);
        return view;
    }
}