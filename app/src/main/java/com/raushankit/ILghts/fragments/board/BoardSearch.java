package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardSearchAdapter;
import com.raushankit.ILghts.dialogs.FilterDialogFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.model.FilterModel;

import java.util.Collections;

public class BoardSearch extends Fragment {
    private static final String TAG = "BoardSearch";

    private ShimmerFrameLayout shimmerFrameLayout;
    private BoardSearchAdapter adapter;
    private Snackbar snackbar;
    private FilterModel filterModel;
    private FilterDialogFragment filterDialogFragment;

    public BoardSearch() {
        // Required empty public constructor
    }

    public static BoardSearch newInstance() {
        BoardSearch fragment = new BoardSearch();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        adapter = new BoardSearchAdapter(Collections.emptyMap());
        filterDialogFragment = FilterDialogFragment.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_search, container, false);
        snackbar = Snackbar.make(view, R.string.failed_to_load_data, Snackbar.LENGTH_INDEFINITE);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        FloatingActionButton filterButton = view.findViewById(R.id.board_search_list_filter_button);
        snackText.setMaxLines(5);
        snackbar.setAction(R.string.retry, v -> {
            //TODO: do retry
        });
        filterButton.setOnClickListener(v -> filterDialogFragment.show(getChildFragmentManager(), "filterModel"));
        RecyclerView recyclerView = view.findViewById(R.id.board_search_list);
        shimmerFrameLayout = view.findViewById(R.id.board_search_shimmer_container);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_SEARCH_BOARDS);
        args.putInt(BoardConst.CURRENT_FRAG_MENU_ID, R.id.bottom_nav_more_boards);
        getParentFragmentManager()
                .setFragmentResult(BoardConst.REQUEST_KEY, args);
    }

}