package com.raushankit.ILghts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.raushankit.ILghts.fragments.board.BoardFragment;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.viewModel.BoardCommViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";

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


        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = BoardFragment.newInstance();
            Bundle args = new Bundle();
            args.putString("user_id", mAuth.getUid());
            fragment.setArguments(args);
            ft.replace(R.id.board_main_frame, fragment).commit();
        }
    }

    private void init(){
        toolbar = findViewById(R.id.board_main_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
}