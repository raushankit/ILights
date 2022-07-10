package com.raushankit.ILghts;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.raushankit.ILghts.forms.board.BoardCredentials;
import com.raushankit.ILghts.forms.board.BoardPinSelection;
import com.raushankit.ILghts.forms.board.BoardTitle;
import com.raushankit.ILghts.forms.board.BoardVerification;
import com.raushankit.ILghts.utils.FormFlowLine;
import com.raushankit.ILghts.viewModel.BoardFormCommViewModel;

public class BoardForm extends AppCompatActivity {
    private static final String TAG = "BoardForm";

    public static final String FORM1 = "basic_details";
    public static final String FORM2 = "pin_details";
    public static final String FORM3 = "credential_details";
    public static final String FORM4 = "review_form";
    public static final String ON_PROGRESS_BAR = "on_progress_bar";
    public static final String OFF_PROGRESS_BAR = "off_progress_bar";

    private String CURRENT_FRAGMENT = "empty";

    private ProgressBar progressBar;
    private MaterialToolbar toolbar;
    private FormFlowLine formFlowLine;

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

        progressBar = findViewById(R.id.board_form_progress_bar);
        toolbar = findViewById(R.id.board_form_toolbar);
        formFlowLine = findViewById(R.id.board_form_flow_line);
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });
        progressBar.setVisibility(View.GONE);

        BoardFormCommViewModel boardFormCommViewModel = new ViewModelProvider(this).get(BoardFormCommViewModel.class);
        boardFormCommViewModel.getData().observe(this, s -> {
            if(TextUtils.isEmpty(s))return;
            switch (s){
                case OFF_PROGRESS_BAR:
                    progressBar.setVisibility(View.GONE);
                    break;
                case ON_PROGRESS_BAR:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case FORM1:
                case FORM2:
                case FORM3:
                case FORM4:
                    switchFrags(s);
                    boardFormCommViewModel.putData(null);
                    break;
                default:
                    Log.w(TAG, "onCreate: unknown event type");
            }
        });


        if(savedInstanceState == null){
            switchFrags(FORM1);
        }
    }

    private void switchFrags(String key){
        if(key == null || key.equals(CURRENT_FRAGMENT)) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (key) {
            case FORM1:
                formFlowLine.setActiveIndex(1);
                ft.replace(R.id.board_form_frame, BoardTitle.newInstance())
                        .addToBackStack(null).commit();
                CURRENT_FRAGMENT = key;
                break;
            case FORM2:
                formFlowLine.setActiveIndex(2);
                ft.replace(R.id.board_form_frame, BoardPinSelection.newInstance())
                        .addToBackStack(null).commit();
                CURRENT_FRAGMENT = key;
                break;
            case FORM3:
                formFlowLine.setActiveIndex(3);
                ft.replace(R.id.board_form_frame, BoardCredentials.newInstance())
                        .addToBackStack(null).commit();
                CURRENT_FRAGMENT = key;
                break;
            case FORM4:
                formFlowLine.setActiveIndex(4);
                ft.replace(R.id.board_form_frame, BoardVerification.newInstance())
                        .addToBackStack(null).commit();
                CURRENT_FRAGMENT = key;
                break;
        }
    }
}