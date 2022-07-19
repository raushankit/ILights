package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardMemberAdapter;
import com.raushankit.ILghts.factory.BoardMemberViewModelFactory;
import com.raushankit.ILghts.viewModel.BoardMemberViewModel;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class BoardEditMemberFragment extends Fragment {
    private static final String TAG = "BoardEditMemberFragment";
    private BoardMemberAdapter adapter;
    public BoardEditMemberFragment() {
        // Required empty public constructor
    }

    public static BoardEditMemberFragment newInstance() {
        Log.i(TAG, "newInstance() called");
        BoardEditMemberFragment fragment = new BoardEditMemberFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_edit_member, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.board_fragment_edit_member_recyclerview);
        adapter = new BoardMemberAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated() called");
        BoardMemberViewModel viewModel = new ViewModelProvider(requireActivity(),
                new BoardMemberViewModelFactory(requireActivity().getApplication(), "-N71eq_-_hqz_ZiWoph3", 12L))
                .get(BoardMemberViewModel.class);

        viewModel.getFlowable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(boardAuthUserPagingData -> adapter.submitData(getLifecycle(), boardAuthUserPagingData));
    }
}