package com.raushankit.ILghts.fragments.board;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.BoardForm;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardUserItemAdapter;
import com.raushankit.ILghts.dialogs.BoardBottomSheetFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.storage.VolleyRequest;
import com.raushankit.ILghts.utils.BoardFabLayout;
import com.raushankit.ILghts.viewModel.BoardDataViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class BoardFragment extends Fragment {
    public static final String TAG = "BoardFragment";
    private static final String DELETE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=";
    private View view;
    private VolleyRequest requestQueue;

    private RecyclerView recyclerView;
    private BoardDataViewModel boardDataViewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private BoardUserItemAdapter adapter;
    private ActivityResultLauncher<Intent> addBoardLauncher;
    private String uid;
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
                default:
                    Log.i(TAG, "BoardFragment: another type option" + whichButton);
            }
        };
    }

    public static BoardFragment newInstance(String userId) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
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
            uid = args.getString(BoardConst.USER_ID);
        }
        boardDataViewModel = new ViewModelProvider(requireActivity())
                .get(BoardDataViewModel.class);
        requestQueue = VolleyRequest.newInstance(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_board, container, false);
        BoardFabLayout boardFabLayout = new BoardFabLayout(view.findViewById(R.id.board_fab_button_layout), new WeakReference<>(requireContext()));
        addBoardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        Snackbar.make(view, getString(R.string.board_form_success_message, receiveIntent.getStringExtra(BoardFormConst.TITLE)), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    if(result.getResultCode() == Activity.RESULT_CANCELED && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        deleteBoardCredentials(receiveIntent.getStringExtra(BoardFormConst.API_KEY),
                                receiveIntent.getStringExtra(BoardFormConst.ID_TOKEN));
                    }
                }
        );
        recyclerView = view.findViewById(R.id.board_fragment_recyclerview);
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
        recyclerView.setOnScrollChangeListener((v,i1,i2,i3,i4)->{
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
        });

        return view;
    }

    private void deleteBoardCredentials(String apiKey, String idToken){
        if(TextUtils.isEmpty(apiKey) || TextUtils.isEmpty(idToken)){
            return;
        }
        JSONObject js = new JSONObject();
        try{
            js.put("idToken", idToken);
        }catch (JSONException e){
            Log.w(TAG, "sendRequest: ", e);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, DELETE_URL + apiKey, js, response -> Log.i(TAG, "deleteBoardCredentials: deleted credentials"), error -> Log.i(TAG, "deleteBoardCredentials: error deleting credentials")){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
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
    public void onDestroy() {
        super.onDestroy();
        view = null;
    }
}