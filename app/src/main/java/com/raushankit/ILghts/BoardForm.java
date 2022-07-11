package com.raushankit.ILghts;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.forms.board.BoardCredentials;
import com.raushankit.ILghts.forms.board.BoardPinSelection;
import com.raushankit.ILghts.forms.board.BoardTitle;
import com.raushankit.ILghts.forms.board.BoardVerification;
import com.raushankit.ILghts.utils.FormFlowLine;
import com.raushankit.ILghts.viewModel.BoardFormCommViewModel;

public class BoardForm extends AppCompatActivity {
    private static final String TAG = "BoardForm";

    private ProgressBar progressBar;
    private MaterialToolbar toolbar;
    private FormFlowLine formFlowLine;
    private boolean isBackPressedTwice = false;
    private Runnable backPressRunnable;

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
        backPressRunnable = () -> isBackPressedTwice = false;

        BoardFormCommViewModel boardFormCommViewModel = new ViewModelProvider(this).get(BoardFormCommViewModel.class);
        boardFormCommViewModel.getData().observe(this, s -> {
            if(TextUtils.isEmpty(s))return;
            switch (s){
                case BoardFormConst.OFF_PROGRESS_BAR:
                    progressBar.setVisibility(View.GONE);
                    break;
                case BoardFormConst.ON_PROGRESS_BAR:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case BoardFormConst.FORM1:
                case BoardFormConst.FORM2:
                case BoardFormConst.FORM3:
                case BoardFormConst.FORM4:
                    switchFrags(s);
                    boardFormCommViewModel.putData(null);
                    break;
                default:
                    Log.w(TAG, "onCreate: unknown event type");
            }
        });

        getSupportFragmentManager().setFragmentResultListener(BoardFormConst.REQUEST, this, (requestKey, result) -> {
            if(!TextUtils.equals(requestKey, BoardFormConst.REQUEST)) return;
            if(result.containsKey(BoardFormConst.CHANGE_FRAGMENT)){
                switchFrags(result.getString(BoardFormConst.CHANGE_FRAGMENT));
            }
            if(result.containsKey(BoardFormConst.CURRENT_FRAGMENT)){
                formFlowLine.setActiveIndex(result.getInt(BoardFormConst.CURRENT_FRAGMENT));
            }
        });


        if(savedInstanceState == null){
            switchFrags(BoardFormConst.FORM1);
        }
    }

    private void switchFrags(String key){
        Log.e(TAG, "switchFrags: key = " + key);
        if(key == null || key.equals("FORM" + formFlowLine.getActiveIndex())) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (key) {
            case BoardFormConst.FORM1:
                ft.replace(R.id.board_form_frame, BoardTitle.newInstance())
                        .addToBackStack(null).commit();
                break;
            case BoardFormConst.FORM2:
                ft.replace(R.id.board_form_frame, BoardPinSelection.newInstance())
                        .addToBackStack(null).commit();
                break;
            case BoardFormConst.FORM3:
                ft.replace(R.id.board_form_frame, BoardCredentials.newInstance())
                        .addToBackStack(null).commit();
                break;
            case BoardFormConst.FORM4:
                ft.replace(R.id.board_form_frame, BoardVerification.newInstance())
                        .addToBackStack(null).commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(formFlowLine.getActiveIndex() == 1){
            if(isBackPressedTwice){
                setResult(RESULT_CANCELED);
                finish();
            }
            isBackPressedTwice = true;
            Snackbar.make(findViewById(android.R.id.content), R.string.twice_back_press_message, Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(backPressRunnable, 2000);
        }else{
            super.onBackPressed();
        }
    }
}