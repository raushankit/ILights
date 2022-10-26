package com.raushankit.ILghts.fragments.board;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.BoardSearchUsers;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardUserItemAdapter;
import com.raushankit.ILghts.dialogs.BoardBottomSheetFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.viewModel.BoardDataViewModel;

import java.util.Objects;

public class BoardFragment extends Fragment {
    public static final String TAG = "BoardFragment";
    private View view;
    private RecyclerView recyclerView;
    private BoardDataViewModel boardDataViewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private BoardUserItemAdapter adapter;
    private ActivityResultLauncher<Intent> addBoardUsersLauncher;
    private LinearLayout noDataLayout;
    private String userId;
    private final BoardBottomSheetFragment.WhichButtonCLickedListener listener;

    public BoardFragment() {
        listener = (whichButton, details) -> {
            Bundle args = new Bundle();
            switch (whichButton){
                case COPY_ID:
                    copyToClipBoard(details.getBoardId());
                    break;
                case EDIT_MEMBERS:
                    args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_EDIT_MEMBER);
                    args.putParcelable(BoardConst.BOARD_DATA, details);
                    getParentFragmentManager()
                            .setFragmentResult(BoardConst.REQUEST_KEY, args);
                    break;
                case EDIT_DETAILS:
                    args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_EDIT_DETAILS);
                    args.putParcelable(BoardConst.BOARD_DATA, details);
                    getParentFragmentManager()
                            .setFragmentResult(BoardConst.REQUEST_KEY, args);
                    break;
                case SHOW_CREDENTIALS:
                    args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_CRED_DETAILS);
                    args.putParcelable(BoardConst.BOARD_DATA, details);
                    getParentFragmentManager()
                            .setFragmentResult(BoardConst.REQUEST_KEY, args);
                    break;
                case ADD_MEMBERS:
                    Intent intent = new Intent(requireActivity(), BoardSearchUsers.class);
                    intent.putExtra("BOARD", details);
                    addBoardUsersLauncher.launch(intent);
                    break;
                default:
                    Log.i(TAG, "BoardFragment: another type option" + whichButton);
            }
        };
    }

    public static BoardFragment newInstance(String userId) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        Log.i(TAG, "newInstance: " + userId);
        args.putString(BoardConst.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if(args != null){
            userId = args.getString(BoardConst.USER_ID);
        }
        boardDataViewModel = new ViewModelProvider(requireActivity())
                .get(BoardDataViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_board, container, false);
        addBoardUsersLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Snackbar.make(view, R.string.add_users_successful, BaseTransientBottomBar.LENGTH_SHORT)
                                .show();
                    }
                }
        );
        recyclerView = view.findViewById(R.id.board_fragment_recyclerview);
        noDataLayout = view.findViewById(R.id.board_fragment_no_data);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.board_list_item_divider_decoration, requireActivity().getTheme())));
        recyclerView.addItemDecoration(decoration);
        adapter = new BoardUserItemAdapter((type, data) -> {
            switch (type){
                case COPY_ID:
                    copyToClipBoard(data.getBoardId());
                    break;
                case GO_TO_BOARD:
                    Log.i(TAG, "onCreateView:" + type + " -> " + data);
                    break;
                case SHOW_OPTIONS:
                    BoardBottomSheetFragment fragment = BoardBottomSheetFragment.newInstance(data);
                    fragment.addOnClickListener(listener);
                    fragment.show(getChildFragmentManager(), type.name());
                    break;
                case LIKE_BOARD:
                    boardDataViewModel.setFavouriteBoard(data.getBoardId(), !data.isFav());
                    break;
                default:
                    Log.i(TAG, "onCreateView: unknown event");
            }
        });
        shimmerFrameLayout = view.findViewById(R.id.board_fragment_shimmer_container);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setAdapter(adapter);
        /*recyclerView.setOnScrollChangeListener((v,i1,i2,i3,i4)->{
            if(Math.abs(i4) > 25){
                boardFabLayout.closeOptions();
            }
        });
        boardFabLayout.setOnClickListener(whichButton -> {
            boardFabLayout.closeOptions();
            switch (whichButton){
                case ADD_NEW_BOARD:
                    Intent intent = new Intent(requireActivity(), BoardForm.class);
                    intent.putExtra("user_id", uid);
                    addBoardLauncher.launch(intent);
                    break;
                case GET_PUBLIC_BOARDS:
                    break;
                default:
                    Log.w(TAG, "onCreateView: unknown click item");
            }
        });*/

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boardDataViewModel.getData().observe(getViewLifecycleOwner(), dataList -> {
            if (shimmerFrameLayout.isShimmerStarted()) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            Log.w(TAG, "onViewCreated: list = " + dataList);
            noDataLayout.setVisibility(CollectionUtils.isEmpty(dataList)? View.VISIBLE: View.INVISIBLE);
            adapter.submitList(dataList);
        });
    }

    private void copyToClipBoard(@NonNull String text){
        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("board_id", text);
        clipboardManager.setPrimaryClip(data);
        Snackbar.make(view, R.string.copied_board_id, BaseTransientBottomBar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_BOARD);
        getParentFragmentManager()
                .setFragmentResult(BoardConst.REQUEST_KEY, args);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
    }
}