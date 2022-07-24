package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.viewModel.BoardDataViewModel;

public class BoardCredentialViewer extends Fragment {
    private static final String TAG = "BoardCredentialViewer";
    private BoardRoomUserData data;
    private TextInputLayout titleLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MaterialAutoCompleteTextView usernameText;
    private MaterialAutoCompleteTextView passwordText;

    public BoardCredentialViewer() {
        // Required empty public constructor
    }

    public static BoardCredentialViewer newInstance(BoardRoomUserData data) {
        BoardCredentialViewer fragment = new BoardCredentialViewer();
        Bundle args = new Bundle();
        args.putParcelable(BoardConst.BOARD_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            data = args.getParcelable(BoardConst.BOARD_DATA);
        }else{
            Log.i(TAG, "onCreate: why here empty user data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board_credential_viewer, container, false);
        titleLayout = view.findViewById(R.id.frag_board_credential_viewer_title_layout);
        usernameLayout = view.findViewById(R.id.frag_board_credential_viewer_username_layout);
        passwordLayout = view.findViewById(R.id.frag_board_credential_viewer_password_layout);
        MaterialAutoCompleteTextView titleText = view.findViewById(R.id.frag_board_credential_viewer_title_text);
        usernameText = view.findViewById(R.id.frag_board_credential_viewer_username_input);
        passwordText = view.findViewById(R.id.frag_board_credential_viewer_password_input);
        shimmerFrameLayout = view.findViewById(R.id.frag_board_credential_viewer_shimmer_container);
        shimmerFrameLayout.startShimmer();

        RelativeLayout navLayout = view.findViewById(R.id.frag_board_credential_viewer_nav_button_layout);
        MaterialButton prevButton = navLayout.findViewById(R.id.form_navigation_prev_button);
        MaterialButton nextButton = navLayout.findViewById(R.id.form_navigation_next_button);
        titleText.setText(data.getData().getTitle());
        titleLayout.setHelperText(getString(R.string.board_cred_usage_text, ""));
        nextButton.setVisibility(View.GONE);
        prevButton.setText(R.string.back);
        prevButton.setOnClickListener(view1 -> {
            Bundle args = new Bundle();
            args.putBoolean(BoardConst.SHOW_SNACK_BAR, true);
            getParentFragmentManager()
                    .setFragmentResult(BoardConst.REQUEST_KEY, args);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BoardDataViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(BoardDataViewModel.class);
        viewModel.getCredentials(data.getBoardId());
        Snackbar snackbar = Snackbar.make(view, R.string.failed_to_get_board_cred, BaseTransientBottomBar.LENGTH_INDEFINITE);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);
        snackbar.setAction(R.string.retry, view1 -> viewModel.getCredentials(data.getBoardId()));

        viewModel.getCredentialLiveData()
                .observe(getViewLifecycleOwner(), boardCredModel -> {
                    if(boardCredModel.getId() == null){
                        snackbar.show();
                    }else{
                        if(shimmerFrameLayout.isShimmerVisible()){
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            usernameLayout.setVisibility(View.VISIBLE);
                            passwordLayout.setVisibility(View.VISIBLE);
                        }
                        usernameText.setText(boardCredModel.getUserName());
                        passwordText.setText(boardCredModel.getPassword());
                        titleLayout.setHelperText(getString(R.string.board_cred_usage_text, "UID: " + boardCredModel.getId()));
                    }
                });
    }
}