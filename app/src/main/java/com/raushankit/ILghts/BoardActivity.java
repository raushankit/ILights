package com.raushankit.ILghts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.fragments.board.BoardCredentialViewer;
import com.raushankit.ILghts.fragments.board.BoardEditDetails;
import com.raushankit.ILghts.fragments.board.BoardEditMemberFragment;
import com.raushankit.ILghts.fragments.board.BoardFragment;
import com.raushankit.ILghts.fragments.board.BoardSearch;
import com.raushankit.ILghts.fragments.board.NotificationFragment;
import com.raushankit.ILghts.model.UserCombinedData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.storage.VolleyRequest;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.viewModel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class BoardActivity extends AppCompatActivity {
    public static final String FRAG_REQUEST_KEY = "request_key";
    private static final String TAG = "BoardActivity";
    private static final String DELETE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=";
    private VolleyRequest requestQueue;
    private UserViewModel userViewModel;

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private String currentFrag;
    private final AtomicReference<UserCombinedData> user = new AtomicReference<>();
    private ActivityResultLauncher<Intent> addBoardLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(StringUtils.getTheme(((BaseApp)getApplication()).getThemeIndex()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        mAuth = FirebaseAuth.getInstance();
        requestQueue = VolleyRequest.newInstance(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        init();
        getSupportFragmentManager()
                .setFragmentResultListener(FRAG_REQUEST_KEY, this,
                        (requestKey, result) -> {
                            Log.e(TAG, "onCreate: data = " + requestKey + "  " + result);
                            if(user != null && user.get() != null) {
                                result.putParcelable(BoardConst.USER, user.get().getUser());
                            }
                            if (!TextUtils.equals(requestKey, FRAG_REQUEST_KEY)) {
                                return;
                            }
                            if (result.containsKey(BoardConst.WHICH_FRAG)) {
                                switchFrag(result);
                            }
                            if(result.containsKey(BoardConst.DELETE_CREDS)) {
                                deleteBoardCredentials(result.getString(BoardConst.DELETE_CREDS));
                            }
                            else if (result.containsKey(BoardConst.SHOW_SNACK_BAR)) {
                                if (result.containsKey(BoardConst.SNACK_MESSAGE)) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                                    result.getInt(BoardConst.SNACK_MESSAGE), BaseTransientBottomBar.LENGTH_SHORT)
                                            .show();
                                }
                                getSupportFragmentManager().popBackStackImmediate();
                            } else if (result.containsKey(BoardConst.CURRENT_FRAG)) {
                                currentFrag = result.getString(BoardConst.CURRENT_FRAG);
                                bottomNavigationView.setSelectedItemId(result.getInt(BoardConst.CURRENT_FRAG_MENU_ID, R.id.bottom_nav_board));
                            }
                        });

        Log.e(TAG, "onCreate: savedInsState = " + (savedInstanceState == null));

        if (savedInstanceState == null) {
            userViewModel.getCombinedData()
                    .observe(this, uc -> {
                        user.set(uc);
                        switchFrag(new Bundle());
                    });
        }
    }

    @Override
    public Resources.Theme getTheme() {
        return super.getTheme();
    }

    private void switchFrag(Bundle result) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String key = result.getString(BoardConst.WHICH_FRAG);
        // TODO: 28-07-2022 must change this to fragment board
        key = key == null ? BoardConst.FRAG_BOARD : key;
        if(key.equals(currentFrag)) {
            Log.i(TAG, "switchFrag: same fragment: " + key);
            return;
        }
        switch (key) {
            case BoardConst.FRAG_BOARD:
                ft.replace(R.id.board_main_frame,
                                BoardFragment.newInstance(mAuth.getUid(), user.get().getUser(), user.get().getApiKey()))
                        .commit();
                break;
            case BoardConst.FRAG_EDIT_MEMBER:
                BoardRoomUserData data = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? result.getParcelable(BoardConst.BOARD_DATA, BoardRoomUserData.class)
                        : result.getParcelable(BoardConst.BOARD_DATA);
                ft.replace(R.id.board_main_frame,
                                BoardEditMemberFragment.newInstance(data))
                        .addToBackStack(BoardConst.FRAG_EDIT_MEMBER)
                        .commit();
                break;
            case BoardConst.FRAG_EDIT_DETAILS:
                ft.replace(R.id.board_main_frame,
                                BoardEditDetails.newInstance(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                        ? result.getParcelable(BoardConst.BOARD_DATA, BoardRoomUserData.class)
                                        : result.getParcelable(BoardConst.BOARD_DATA)))
                        .addToBackStack(BoardConst.FRAG_EDIT_DETAILS)
                        .commit();
                break;
            case BoardConst.FRAG_CRED_DETAILS:
                ft.replace(R.id.board_main_frame,
                                BoardCredentialViewer.newInstance(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                        ? result.getParcelable(BoardConst.BOARD_DATA, BoardRoomUserData.class)
                                        : result.getParcelable(BoardConst.BOARD_DATA)))
                        .addToBackStack(BoardConst.FRAG_CRED_DETAILS)
                        .commit();
                break;
            case BoardConst.FRAG_NOTIFICATION:
                ft.replace(R.id.board_main_frame,
                                NotificationFragment.newInstance(Objects.requireNonNull(mAuth.getUid())))
                        .addToBackStack(BoardConst.FRAG_NOTIFICATION)
                        .commit();
                break;
            case BoardConst.FRAG_SEARCH_BOARDS:
                ft.replace(R.id.board_main_frame,
                                BoardSearch.newInstance(user.get().getUser()))
                        .addToBackStack(BoardConst.FRAG_SEARCH_BOARDS)
                        .commit();
                break;
            default:
                Log.i(TAG, "switchFrag: some other key " + key);
        }
    }

    private void init() {
        MaterialToolbar toolbar = findViewById(R.id.board_main_toolbar);
        bottomNavigationView = findViewById(R.id.board_activity_bottom_navigation);
        setNavMenu();
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        toolbar.setNavigationOnClickListener(v -> startActivity(settingsIntent));
        addBoardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent receiveIntent = result.getData();
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.board_form_success_message, receiveIntent.getStringExtra(BoardFormConst.TITLE)), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    if (result.getResultCode() == Activity.RESULT_CANCELED && result.getData() != null) {
                        Intent receiveIntent = result.getData();
                        deleteBoardCredentials(receiveIntent.getStringExtra(BoardFormConst.ID_TOKEN));
                    }
                }
        );
    }

    private void deleteBoardCredentials(String idToken) {
        String apiKey = user.get().getApiKey();
        if (TextUtils.isEmpty(apiKey) || TextUtils.isEmpty(idToken)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("idToken", idToken);
        } catch (JSONException e) {
            Log.w(TAG, "sendRequest: ", e);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, DELETE_URL + apiKey, js, response -> Log.i(TAG, "deleteBoardCredentials: deleted credentials"), error -> Log.i(TAG, "deleteBoardCredentials: error deleting credentials")) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void setNavMenu() {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem itemHome = menu.getItem(0);
        MenuItem itemAdd = menu.getItem(1);
        MenuItem itemMore = menu.getItem(2);
        MenuItem itemNotification = menu.getItem(3);
        BadgeDrawable notificationsBadge = bottomNavigationView.getOrCreateBadge(itemNotification.getItemId());
        userViewModel.getRoleData()
                .observe(this, role -> {
                    itemAdd.setVisible(role.getAccessLevel() >= 2);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                });
        userViewModel.countUnseenNotifications().observe(this, num -> {
            if(num != null && num > 0) {
                notificationsBadge.setNumber(num);
            } else {
                notificationsBadge.clearNumber();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.w(TAG, "setNavMenu: id = " + id);
            if (id == itemAdd.getItemId()) {
                Intent intent = new Intent(this, BoardForm.class);
                intent.putExtra(BoardConst.USER_ID, mAuth.getUid());
                intent.putExtra(BoardConst.USER, user.get().getUser());
                intent.putExtra(BoardFormConst.API_KEY, user.get().getApiKey());
                addBoardLauncher.launch(intent);
                return true;
            } else if (id == itemMore.getItemId()) {
                Bundle args = new Bundle();
                args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_SEARCH_BOARDS);
                switchFrag(args);
                return true;
            } else if (id == itemNotification.getItemId()) {
                Bundle args = new Bundle();
                args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_NOTIFICATION);
                switchFrag(args);
                return true;
            } else if (id == itemHome.getItemId()) {
                Bundle args = new Bundle();
                args.putString(BoardConst.WHICH_FRAG, BoardConst.FRAG_BOARD);
                switchFrag(args);
                return true;
            }
            return false;
        });

    }
}