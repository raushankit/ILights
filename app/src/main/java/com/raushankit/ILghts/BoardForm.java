package com.raushankit.ILghts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.forms.board.BoardPinSelection;
import com.raushankit.ILghts.forms.board.BoardTitle;
import com.raushankit.ILghts.fragments.board.BoardFragment;

public class BoardForm extends AppCompatActivity {

    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board_form);
        // setTheme(R.style.Theme_ILights_1);

        progressBar = findViewById(R.id.board_form_progress_bar);
        toolbar = findViewById(R.id.board_form_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // window.setStatusBarColor(getColor(R.color.primary_status_bar_color));

        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.board_form_frame, BoardPinSelection.newInstance()).commit();
        }
    }
}