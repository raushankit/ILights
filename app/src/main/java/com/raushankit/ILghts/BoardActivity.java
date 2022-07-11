package com.raushankit.ILghts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.fragments.board.BoardFragment;

public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board);
        init();

        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.board_main_frame, BoardFragment.newInstance()).commit();
        }
    }

    private void init(){
        toolbar = findViewById(R.id.board_main_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
}