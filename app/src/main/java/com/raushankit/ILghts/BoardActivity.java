package com.raushankit.ILghts;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.fragments.board.BoardCredentialViewer;
import com.raushankit.ILghts.fragments.board.BoardEditDetails;
import com.raushankit.ILghts.fragments.board.BoardEditMemberFragment;
import com.raushankit.ILghts.fragments.board.BoardFragment;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.viewModel.BoardCommViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";
    public static final String FRAG_REQUEST_KEY = "request_key";
    private BoardCommViewModel boardCommViewModel;

    private MaterialToolbar toolbar;
    private FirebaseAuth mAuth;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board);
        mAuth = FirebaseAuth.getInstance();
        init();
        boardCommViewModel = new ViewModelProvider(this).get(BoardCommViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserData().observe(this, user -> {
            mUser = user;
            boardCommViewModel.setData(new Pair<>(mAuth.getUid(), user));
        });
        getSupportFragmentManager().setFragmentResultListener(FRAG_REQUEST_KEY, this,
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
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
}