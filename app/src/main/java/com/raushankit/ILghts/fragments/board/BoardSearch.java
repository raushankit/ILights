package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardSearchAdapter;
import com.raushankit.ILghts.dialogs.FilterDialogFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.viewModel.BoardSearchViewModel;

import java.util.stream.Collectors;

public class BoardSearch extends Fragment {
    private static final String TAG = "BoardSearch";

    private Snackbar snackbar;
    private ShimmerFrameLayout shimmerFrameLayout;
    private BoardSearchAdapter adapter;
    private FilterModel filterModel;
    private FilterDialogFragment filterDialogFragment;
    private LinearProgressIndicator progressIndicator;
    private BoardSearchViewModel viewModel;
    private User userTemp;

    public BoardSearch() {
        // Required empty public constructor
    }

    public static BoardSearch newInstance(@NonNull User user) {
        BoardSearch fragment = new BoardSearch();
        Bundle args = new Bundle();
        args.putParcelable("user-data", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userTemp = null;
        if(args != null) {
            userTemp = args.getParcelable("user-data");
        }
        filterDialogFragment = FilterDialogFragment.newInstance();
        filterModel = new FilterModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_search, container, false);
        FloatingActionButton filterButton = view.findViewById(R.id.board_search_list_filter_button);
        filterButton.setOnClickListener(v -> {
            filterDialogFragment.setFilterModel(filterModel);
            filterDialogFragment.show(getChildFragmentManager(), "filterModel");
        });
        snackbar = Snackbar.make(view, R.string.something_went_wrong, BaseTransientBottomBar.LENGTH_INDEFINITE);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);
        final User user = userTemp;
        adapter = new BoardSearchAdapter(value -> {
            Log.i(TAG, "onCreate: value: " + value) ;
            if(user == null) {
                Log.w(TAG, "onCreate: must not reach here user cannot be null");
            } else {
                viewModel.getAccess(value.getThird(), user, value.getSecond(), v -> {
                    if(v == null) {
                        adapter.updateUserBoards(value.getThird().getBoardId(), false);
                        adapter.notifyItemChanged(value.getFirst());
                    } else {
                        Snackbar.make(view, R.string.failed_to_request_access, BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.board_search_list);
        progressIndicator = view.findViewById(R.id.board_search_progress_bar);
        shimmerFrameLayout = view.findViewById(R.id.board_search_shimmer_container);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity())
                .get(BoardSearchViewModel.class);
        filterDialogFragment.addCallBack(value ->  {
            Log.d(TAG, "onCreate: model = " + value);
            value.setRetry(false);
            value.setNextPage(false);
            viewModel.setFilterModel(value.clone());
            filterModel = value;
        });
        adapter.addTrigger(value -> {
            Log.d(TAG, "onViewCreated: should reach here");
            filterModel.setNextPage(true);
            filterModel.setRetry(false);
            viewModel.setBooleans(false, true);
        });
        filterModel.setType(FilterModel.Type.NULL);
        viewModel.setFilterModel(filterModel);
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), data -> {
            Log.d(TAG, "onCreateView: got data: " + data);
            adapter.setUserBoards(data.getUserBoardIds());
            if(data.getData().isEmpty() && !data.isNoData()) {
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
            }
            if(data.isNoData() || !data.getData().isEmpty() || data.getError() != null) {
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                progressIndicator.setVisibility(View.GONE);
                adapter.submitList(data.getData().values().stream().sorted((o1, o2) -> {
                    boolean c1 = data.getUserBoardIds().containsKey(o1.getBoardId());
                    boolean c2 = data.getUserBoardIds().containsKey(o2.getBoardId());
                    return c1 && c2? 0: c1? 1: -1;
                }).collect(Collectors.toList()));
                if(data.isNoData()) {
                    snackbar.setText(R.string.no_data_found)
                            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                            .show();
                }
            }
            if(data.getPageNumber() > 1 && data.isLoading()) {
                progressIndicator.setVisibility(View.VISIBLE);
            }
            if(data.getError() != null) {
                snackbar.setText(R.string.something_went_wrong)
                        .setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, v -> {
                            filterModel.setNextPage(false);
                            filterModel.setRetry(true);
                            viewModel.setBooleans(true, false);
                    snackbar.dismiss();
                });
            }
        });
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