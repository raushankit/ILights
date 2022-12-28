package com.raushankit.ILghts.fragments.board;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.BoardControl;
import com.raushankit.ILghts.BoardSearchUsers;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.BoardUserItemAdapter;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.dialogs.BoardBottomSheetFragment;
import com.raushankit.ILghts.dialogs.LoadingDialogFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.storage.VolleyRequest;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.BoardDataViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BoardFragment extends Fragment {
    public static final String TAG = "BoardFragment";
    private View view;
    private RecyclerView recyclerView;
    private BoardDataViewModel boardDataViewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private BoardUserItemAdapter adapter;
    private LoadingDialogFragment loadingDialogFragment;
    private AlertDialogFragment alertDialogFragment;
    private VolleyRequest requestQueue;
    private ActivityResultLauncher<Intent> addBoardUsersLauncher;
    private ActivityResultLauncher<Intent> goToSwitchesLauncher;
    private LinearLayout noDataLayout;
    private String userId;
    private User user;
    private String apiKey;
    private BoardBottomSheetFragment.WhichButtonCLickedListener listener;

    public BoardFragment() {

    }

    public static BoardFragment newInstance(String userId, User user, String apiKey) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(BoardConst.USER_ID, userId);
        args.putParcelable(BoardConst.USER, user);
        args.putString(BoardFormConst.API_KEY, apiKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args1 = getArguments();
        requestQueue = VolleyRequest.newInstance(requireActivity());
        if(args1 != null){
            userId = args1.getString(BoardConst.USER_ID);
            user = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? args1.getParcelable(BoardConst.USER, User.class)
                    : args1.getParcelable(BoardConst.USER);
            apiKey = args1.getString(BoardFormConst.API_KEY);
        }
        alertDialogFragment = AlertDialogFragment.newInstance(R.string.confirm_action, true, true);
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        loadingDialogFragment.setTitle(R.string.deleting_board_title);
        loadingDialogFragment.setTitle(R.string.please_wait);
        listener = (whichButton, details) -> {
            Bundle args = new Bundle();
            Log.e(TAG, "BoardFragment: another type option: " + whichButton);
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
                    intent.putExtra(BoardConst.BOARD_DATA, details);
                    addBoardUsersLauncher.launch(intent);
                    break;
                case GO_TO_BOARD:
                    Intent intent1 = new Intent(requireActivity(), BoardControl.class);
                    intent1.putExtra(BoardConst.BOARD_DATA, details);
                    intent1.putExtra(BoardConst.USER_ID, userId);
                    intent1.putExtra(BoardConst.USER, user);
                    goToSwitchesLauncher.launch(intent1);
                    break;
                case GET_HIGHER_ACCESS:
                    boardDataViewModel.getEditorLevelAccess(details, user, err -> Snackbar.make(view, err != null? err
                            : "Request sent to board owner", BaseTransientBottomBar.LENGTH_LONG).show());
                    break;
                case LEAVE_BOARD:
                    alertDialogFragment.setBodyString("leaving this board will remove all the access from this board. You would need to request again to get access of this board. Are you sure ?");
                    args.putParcelable("data", details);
                    args.putString("type", whichButton.name());
                    alertDialogFragment.setExtraArgs(args);
                    alertDialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
                    break;
                case DELETE_BOARD:
                    alertDialogFragment.setBodyString("Deleting this board will remove all users and data will be permanently removed and cannot be recovered. Are you sure ?");
                    args.putParcelable("data", details);
                    args.putString("type", whichButton.name());
                    alertDialogFragment.setExtraArgs(args);
                    alertDialogFragment.show(getChildFragmentManager(), AlertDialogFragment.TAG);
                    break;
                default:
                    Log.i(TAG, "BoardFragment: another type option: " + whichButton);
            }
        };
        alertDialogFragment.addWhichButtonClickedListener(wh -> {
            alertDialogFragment.dismiss();
            if(wh == AlertDialogFragment.WhichButton.NEGATIVE) {
                return;
            }
            Bundle alertDialogArgs = alertDialogFragment.getExtraArgs();
            String type = alertDialogArgs.getString("type");
            BoardRoomUserData details = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? alertDialogArgs.getParcelable("data", BoardRoomUserData.class)
                    : alertDialogArgs.getParcelable("data");
            if (BoardBottomSheetFragment.WhichButton.DELETE_BOARD.name().equals(type)) {
                boardDataViewModel.getCredentials(details.getBoardId());
                getIdToken(getString(R.string.board_form_credential_username_suffix), apiKey, idToken -> {
                    loadingDialogFragment.show(getChildFragmentManager(), LoadingDialogFragment.TAG);
                    if(idToken == null) {
                        loadingDialogFragment.dismiss();
                    } else {
                        boardDataViewModel.deleteBoard(details, err -> {
                            if(err == null) {
                                Bundle idTokenArgs = new Bundle();
                                idTokenArgs.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_BOARD);
                                idTokenArgs.putInt(BoardConst.CURRENT_FRAG_MENU_ID, R.id.bottom_nav_board);
                                getParentFragmentManager()
                                        .setFragmentResult(BoardConst.REQUEST_KEY, idTokenArgs);
                            } else {
                                Snackbar.make(view, err, BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            loadingDialogFragment.dismiss();
                        });
                    }
                });
            }
            else if (BoardBottomSheetFragment.WhichButton.LEAVE_BOARD.name().equals(type)) {
                boardDataViewModel.leaveBoard(user, details, err -> {
                    if(err == null) {
                        Log.i(TAG, "onCreate: left board");
                    } else {
                        Snackbar.make(view, err, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
            }
        });
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
        goToSwitchesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Snackbar.make(view, R.string.add_users_successful, BaseTransientBottomBar.LENGTH_SHORT)
                                .show();
                    }
                }
        );
        recyclerView = view.findViewById(R.id.board_fragment_recyclerview);
        noDataLayout = view.findViewById(R.id.board_fragment_no_data);
        adapter = new BoardUserItemAdapter((type, data) -> {
            switch (type){
                case COPY_ID:
                    copyToClipBoard(data.getBoardId());
                    break;
                case GO_TO_BOARD:
                    Intent intent1 = new Intent(requireActivity(), BoardControl.class);
                    intent1.putExtra(BoardConst.BOARD_DATA, data);
                    intent1.putExtra(BoardConst.USER_ID, userId);
                    intent1.putExtra(BoardConst.USER, user);
                    goToSwitchesLauncher.launch(intent1);
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

    public void getIdToken(String usernameSuffix, String apiKey, CallBack<String> callback) {
        boardDataViewModel.getCredentialLiveData()
                .observe(this, obs -> {
                    if(obs.getUserName() == null || obs.getPassword() == null) {
                        Snackbar.make(view, R.string.unknown_error, BaseTransientBottomBar.LENGTH_LONG).show();
                        callback.onClick(null);
                        return;
                    }
                    Log.e(TAG, "getIdToken: CREDS = " + obs);
                    final String baseUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";
                    JSONObject js = new JSONObject();
                    try{
                        js.put("email", obs.getUserName() + usernameSuffix);
                        js.put("password", obs.getPassword());
                        js.put("returnSecureToken", true);
                    }catch (JSONException e){
                        Log.w(TAG, "sendRequest: ", e);
                    }
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl + apiKey, js, response -> {
                        try{
                            String idToken = response.getString("idToken");
                            callback.onClick(idToken);
                        } catch (JSONException e) {
                            Log.w(TAG, "onResponse: ", e);
                            Snackbar.make(view, R.string.unknown_error, BaseTransientBottomBar.LENGTH_LONG).show();
                            callback.onClick(null);
                        }

                    }, error -> {
                        String errorString = getString(R.string.unknown_error);
                        if(error.networkResponse == null){
                            errorString = getString(R.string.internet_connection_error);
                        }else{
                            try{
                                JSONObject parent = new JSONObject(new String(error.networkResponse.data));
                                JSONObject errorChild = parent.getJSONObject("error");
                                errorString =  errorChild.getString("message");
                            }catch (JSONException e){
                                Log.w(TAG, "onErrorResponse: ", e);
                            }
                        }
                        Snackbar.make(view, errorString, BaseTransientBottomBar.LENGTH_LONG).show();
                        callback.onClick(null);
                    }){
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(request);
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_BOARD);
        args.putInt(BoardConst.CURRENT_FRAG_MENU_ID, R.id.bottom_nav_board);
        getParentFragmentManager()
                .setFragmentResult(BoardConst.REQUEST_KEY, args);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
    }
}