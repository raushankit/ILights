package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardLoadingAdapter;
import com.raushankit.ILghts.adapter.BoardMemberAdapter;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.factory.BoardMemberViewModelFactory;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.BoardMemberViewModel;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class BoardEditMemberFragment extends Fragment {
    private static final String TAG = "BoardEditMemberFragment";
    private BoardMemberAdapter adapter;
    private BoardRoomUserData boardData;
    private final AlertDialogFragment dialogFragment;
    private Pair<BoardMemberAdapter.WhichButton,BoardAuthUser> clickedData;
    private CallBack<DatabaseError> callBack;

    public BoardEditMemberFragment() {
        // Required empty public constructor
        dialogFragment = AlertDialogFragment.newInstance(
                R.string.confirm_action, true, true
        );
    }

    public static BoardEditMemberFragment newInstance(BoardRoomUserData data) {
        Log.i(TAG, "newInstance() called");
        BoardEditMemberFragment fragment = new BoardEditMemberFragment();
        Bundle args = new Bundle();
        args.putParcelable(BoardConst.BOARD_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            boardData = args.getParcelable(BoardConst.BOARD_DATA);
            Log.i(TAG, "onCreate: BoardData = " + boardData);
        }else{
            getParentFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_edit_member, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.board_fragment_edit_member_recyclerview);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.board_fragment_edit_member_shimmer_container);
        shimmerFrameLayout.startShimmer();
        adapter = new BoardMemberAdapter();
        PublishSubject<CombinedLoadStates> subject = PublishSubject.create();
        subject.distinctUntilChanged(CombinedLoadStates::getPrepend)
                .filter(combinedLoadStates -> combinedLoadStates.getPrepend() instanceof LoadState.NotLoading)
                .map(combinedLoadStates -> combinedLoadStates.getPrepend().getEndOfPaginationReached())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(aBoolean -> {
                    if(aBoolean){
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
        adapter.addLoadStateListener(loadState -> {
            subject.onNext(loadState);
            return null;
        });
        ConcatAdapter concatAdapter = adapter.withLoadStateFooter(new BoardLoadingAdapter(adapter::retry));
        recyclerView.setAdapter(concatAdapter);
        adapter.addListener((button, user) -> {
            switch (button){
                case DELETE:
                    clickedData = new Pair<>(button, user);
                    dialogFragment.setBodyString(getString(R.string.delete_board_user_body));
                    dialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
                    break;
                case PROMOTE:
                    clickedData = new Pair<>(button, user);
                    dialogFragment.setBodyString(getString(
                            user.getLevel() == 1? R.string.promote_board_user_body_text: R.string.demote_board_user_body_text));
                    dialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
                    break;
                default:
                    Log.w(TAG, "onCreateView: should not come here");
            }
        });
        callBack = error -> {
            if(error != null){
                Snackbar.make(view, error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        };
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated() called");
        BoardMemberViewModel viewModel = new ViewModelProvider(this,
                new BoardMemberViewModelFactory(requireActivity().getApplication(), boardData.getBoardId(), 12L))
                .get(BoardMemberViewModel.class);
        viewModel.getFlowable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(boardAuthUserPagingData -> adapter.submitData(getLifecycle(), boardAuthUserPagingData));
        dialogFragment.addWhichButtonClickedListener(whichButton -> {
            Log.i(TAG, "onViewCreated: " + whichButton);
            if(whichButton == AlertDialogFragment.WhichButton.POSITIVE){
                switch (clickedData.first){
                    case PROMOTE:
                        viewModel.promoteUser(clickedData.second.getUserId(),
                                clickedData.second.getLevel()==1? 2: 1, callBack);
                        break;
                    case DELETE:
                        viewModel.deleteUser(clickedData.second.getUserId(), callBack);
                        break;
                    default:
                        Log.i(TAG, "onViewCreated: should not reach here");
                }
            }
            dialogFragment.dismiss();
        });
    }
}