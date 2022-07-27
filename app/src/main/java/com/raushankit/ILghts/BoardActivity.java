package com.raushankit.ILghts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
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
import com.raushankit.ILghts.fragments.board.NotificationFragment;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.storage.VolleyRequest;
import com.raushankit.ILghts.viewModel.BoardCommViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";
    public static final String FRAG_REQUEST_KEY = "request_key";
    private static final String DELETE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=";
    private VolleyRequest requestQueue;
    private UserViewModel userViewModel;
    private BoardCommViewModel boardCommViewModel;

    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> addBoardLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board);
        mAuth = FirebaseAuth.getInstance();
        requestQueue = VolleyRequest.newInstance(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        boardCommViewModel = new ViewModelProvider(this).get(BoardCommViewModel.class);
        init();
        getSupportFragmentManager()
                .setFragmentResultListener(FRAG_REQUEST_KEY, this,
                (requestKey, result) -> {
                    if(!TextUtils.equals(requestKey, FRAG_REQUEST_KEY)){return;}
                    if(result.containsKey(BoardConst.WHICH_FRAG)){
                        switchFrag(result);
                    }
                    else if(result.containsKey(BoardConst.SHOW_SNACK_BAR)){
                        if(result.containsKey(BoardConst.SNACK_MESSAGE)){
                            Snackbar.make(findViewById(android.R.id.content),
                                            result.getInt(BoardConst.SNACK_MESSAGE), BaseTransientBottomBar.LENGTH_SHORT)
                                    .show();
                        }
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                });

        if(savedInstanceState == null){
            switchFrag(new Bundle());
        }
    }

    @Override
    public Resources.Theme getTheme() {
        return super.getTheme();
    }

    private void switchFrag(Bundle result){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String key = result.getString(BoardConst.WHICH_FRAG);
        key = key == null? BoardConst.FRAG_BOARD: key;
        switch (key){
            case BoardConst.FRAG_BOARD:
                ft.replace(R.id.board_main_frame,
                        BoardFragment.newInstance(mAuth.getUid()))
                        .addToBackStack(null)
                        .commit();
                break;
            case BoardConst.FRAG_EDIT_MEMBER:
                BoardRoomUserData data = result.getParcelable(BoardConst.BOARD_DATA);
                ft.replace(R.id.board_main_frame,
                                BoardEditMemberFragment.newInstance(data))
                        .addToBackStack(null)
                        .commit();
                break;
            case BoardConst.FRAG_EDIT_DETAILS:
                ft.replace(R.id.board_main_frame,
                                BoardEditDetails.newInstance(result.getParcelable(BoardConst.BOARD_DATA)))
                        .addToBackStack(null)
                        .commit();
                break;
            case BoardConst.FRAG_CRED_DETAILS:
                ft.replace(R.id.board_main_frame,
                                BoardCredentialViewer.newInstance(result.getParcelable(BoardConst.BOARD_DATA)))
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                Log.i(TAG, "switchFrag: some other key " + key);
        }
    }

    private void init(){
        toolbar = findViewById(R.id.board_main_toolbar);
        bottomNavigationView = findViewById(R.id.board_activity_bottom_navigation);
        setNavMenu();
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
        addBoardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.board_form_success_message, receiveIntent.getStringExtra(BoardFormConst.TITLE)), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    if(result.getResultCode() == Activity.RESULT_CANCELED && result.getData() != null){
                        Intent receiveIntent = result.getData();
                        deleteBoardCredentials(receiveIntent.getStringExtra(BoardFormConst.API_KEY),
                                receiveIntent.getStringExtra(BoardFormConst.ID_TOKEN));
                    }
                }
        );
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

    private void setNavMenu(){
        Menu menu = bottomNavigationView.getMenu();
        MenuItem itemAdd = menu.getItem(0);
        MenuItem itemMore = menu.getItem(1);
        MenuItem itemNotification = menu.getItem(2);
        menu.setGroupCheckable(0, false, true);
        userViewModel.getRoleData()
                .observe(this, role -> {
                    itemAdd.setVisible(role.getAccessLevel() >= 2);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == itemAdd.getItemId()){
                Intent intent = new Intent(this, BoardForm.class);
                intent.putExtra("user_id", mAuth.getUid());
                addBoardLauncher.launch(intent);
                return true;
            }else if(id == itemMore.getItemId()){
                // TODO: 27-07-2022
                // return true;
            }
            else if(id == itemNotification.getItemId()){
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.board_main_frame,
                                NotificationFragment.newInstance(Objects.requireNonNull(mAuth.getUid())))
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }
}