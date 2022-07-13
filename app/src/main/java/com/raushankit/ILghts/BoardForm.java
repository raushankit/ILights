package com.raushankit.ILghts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.forms.board.BoardCredentials;
import com.raushankit.ILghts.forms.board.BoardPinSelection;
import com.raushankit.ILghts.forms.board.BoardTitle;
import com.raushankit.ILghts.forms.board.BoardVerification;
import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.model.board.BoardPinsModel;
import com.raushankit.ILghts.utils.FormFlowLine;

public class BoardForm extends AppCompatActivity {
    private static final String TAG = "BoardForm";

    private ProgressBar progressBar;
    private FormFlowLine formFlowLine;
    private boolean isBackPressedTwice = false;
    private Runnable backPressRunnable;
    private Intent replyIntent;
    private String apiKey;
    private String idToken;

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
        MaterialToolbar toolbar = findViewById(R.id.board_form_toolbar);
        formFlowLine = findViewById(R.id.board_form_flow_line);
        toolbar.setNavigationOnClickListener(v -> setCancelResult());
        replyIntent = new Intent();
        progressBar.setVisibility(View.GONE);
        backPressRunnable = () -> isBackPressedTwice = false;

        getSupportFragmentManager().setFragmentResultListener(BoardFormConst.REQUEST, this, (requestKey, result) -> {
            if(!TextUtils.equals(requestKey, BoardFormConst.REQUEST)) return;
            if(result.containsKey(BoardFormConst.CHANGE_FRAGMENT)){
                switchFrags(result.getString(BoardFormConst.CHANGE_FRAGMENT));
            }
            if(result.containsKey(BoardFormConst.API_KEY)){
                apiKey = result.getString(BoardFormConst.API_KEY);
            }
            if(result.containsKey(BoardFormConst.ID_TOKEN)){
                idToken = result.getString(BoardFormConst.ID_TOKEN);
            }
            if(result.containsKey(BoardFormConst.CURRENT_FRAGMENT)){
                formFlowLine.setActiveIndex(result.getInt(BoardFormConst.CURRENT_FRAGMENT));
            }
            if(result.containsKey(BoardFormConst.PROGRESS_BAR)){
                progressBar.setVisibility(result.getBoolean(BoardFormConst.PROGRESS_BAR)?View.VISIBLE:View.GONE);
            }
            int count = 0;
            if(result.containsKey(BoardFormConst.FORM1_BUNDLE_KEY)){
                count++;
                replyIntent.putExtra(BoardFormConst.FORM1_BUNDLE_KEY, (BoardBasicModel)result.getParcelable(BoardFormConst.FORM1_BUNDLE_KEY));
            }
            if(result.containsKey(BoardFormConst.FORM2_BUNDLE_KEY)){
                count++;
                replyIntent.putExtra(BoardFormConst.FORM2_BUNDLE_KEY, (BoardPinsModel)result.getParcelable(BoardFormConst.FORM2_BUNDLE_KEY));
            }
            if(result.containsKey(BoardFormConst.FORM3_BUNDLE_KEY)){
                count++;
                replyIntent.putExtra(BoardFormConst.FORM3_BUNDLE_KEY, (BoardCredentialModel)result.getParcelable(BoardFormConst.FORM3_BUNDLE_KEY));
            }
            if(count == 3){
                setResult(Activity.RESULT_OK, replyIntent);
                finish();
            }
        });

        if(savedInstanceState == null){
            switchFrags(BoardFormConst.FORM1);
        }
    }

    private void switchFrags(String key){
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

    private void setCancelResult(){
        replyIntent.putExtra(BoardFormConst.API_KEY, apiKey);
        replyIntent.putExtra(BoardFormConst.ID_TOKEN, idToken);
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(formFlowLine.getActiveIndex() == 1){
            if(isBackPressedTwice){
                setCancelResult();
            }
            isBackPressedTwice = true;
            Snackbar.make(findViewById(android.R.id.content), R.string.twice_back_press_message, Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(backPressRunnable, 2000);
        }else{
            if(progressBar.getVisibility() == View.VISIBLE){
                Log.d(TAG, "onBackPressed: doing work");
                Snackbar.make(findViewById(android.R.id.content), R.string.please_wait, Snackbar.LENGTH_SHORT).show();
            }else{
                super.onBackPressed();
            }
        }
    }
}