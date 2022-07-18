package com.raushankit.ILghts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.raushankit.ILghts.dialogs.LoadingDialogFragment;
import com.raushankit.ILghts.entity.BoardFormConst;
import com.raushankit.ILghts.forms.board.BoardCredentials;
import com.raushankit.ILghts.forms.board.BoardPinSelection;
import com.raushankit.ILghts.forms.board.BoardTitle;
import com.raushankit.ILghts.forms.board.BoardVerification;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.board.BoardAuthUser;
import com.raushankit.ILghts.model.board.BoardBasicModel;
import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.BoardCredentialModel;
import com.raushankit.ILghts.model.board.BoardPinsModel;
import com.raushankit.ILghts.utils.FormFlowLine;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoardForm extends AppCompatActivity {
    private static final String TAG = "BoardForm";
    private static final int OWNER_LEVEL = 3;
    private FormFlowLine formFlowLine;
    private boolean isBackPressedTwice = false;
    private Runnable backPressRunnable;
    private Intent replyIntent;
    private DatabaseReference db;
    private LoadingDialogFragment loadingDialogFragment;
    private String apiKey;
    private String idToken;
    private String uid;
    private String[] visibilityArray = new String[2];
    private User user = new User();

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
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserData().observe(this, user1 -> user = user1);
        db = FirebaseDatabase.getInstance().getReference();
        getUserData();
        visibilityArray = getResources().getStringArray(R.array.board_form_visibility_options);

        // progressBar = findViewById(R.id.board_form_progress_bar);
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        MaterialToolbar toolbar = findViewById(R.id.board_form_toolbar);
        formFlowLine = findViewById(R.id.board_form_flow_line);
        toolbar.setNavigationOnClickListener(v -> setCancelResult());
        replyIntent = new Intent();
        // progressBar.setVisibility(View.GONE);
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
                if(result.getBoolean(BoardFormConst.PROGRESS_BAR)){
                    loadingDialogFragment.setTitle(result.getInt(BoardFormConst.PROGRESS_TITLE));
                    loadingDialogFragment.setMessage(result.getInt(BoardFormConst.PROGRESS_BODY));
                    loadingDialogFragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG);
                }else{
                    loadingDialogFragment.dismiss();
                }
            }
            int count = 0;
            Bundle args = new Bundle();
            if(result.containsKey(BoardFormConst.FORM1_BUNDLE_KEY)){
                count++;
                args.putParcelable(BoardFormConst.FORM1_BUNDLE_KEY, result.getParcelable(BoardFormConst.FORM1_BUNDLE_KEY));
            }
            if(result.containsKey(BoardFormConst.FORM2_BUNDLE_KEY)){
                count++;
                args.putParcelable(BoardFormConst.FORM2_BUNDLE_KEY, result.getParcelable(BoardFormConst.FORM2_BUNDLE_KEY));
            }
            if(result.containsKey(BoardFormConst.FORM3_BUNDLE_KEY)){
                count++;
                args.putParcelable(BoardFormConst.FORM3_BUNDLE_KEY, result.getParcelable(BoardFormConst.FORM3_BUNDLE_KEY));
            }
            if(count == 3){
                if(TextUtils.isEmpty(user.getName())){
                    Snackbar.make(findViewById(android.R.id.content), R.string.board_form_user_retrieval_error, Snackbar.LENGTH_SHORT).show();
                }else{
                    loadingDialogFragment.setTitle(R.string.board_form_creation_title);
                    loadingDialogFragment.setMessage(R.string.board_form_creation_body_auth);
                    loadingDialogFragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG);
                    create_auth(user.getName(), args);
                }
            }
        });

        if(savedInstanceState == null){
            switchFrags(BoardFormConst.FORM1);
        }
    }

    private void getUserData(){
        Intent intent = getIntent();
        if(intent == null){
            setCancelResult();
            return;
        }
        uid = intent.getStringExtra("user_id");
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

    private void create_auth(String name, Bundle args){
        DatabaseReference ref = db.child("board_auth").push();
        BoardAuthUser user = new BoardAuthUser(name, OWNER_LEVEL);
        ref.child(uid).setValue(user, (error, ref1) -> {
            if(error == null){
                loadingDialogFragment.setMessage(R.string.board_form_creation_body_main);
                create_new_board(ref.getKey(), args);
            }else{
                loadingDialogFragment.dismiss();
                Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void create_new_board(String boardId, Bundle args){
        BoardBasicModel basicModel = args.getParcelable(BoardFormConst.FORM1_BUNDLE_KEY);
        BoardPinsModel pinsModel = args.getParcelable(BoardFormConst.FORM2_BUNDLE_KEY);
        BoardCredentialModel credModel = args.getParcelable(BoardFormConst.FORM3_BUNDLE_KEY);

        Log.w(TAG, "create_new_board: id = " + boardId + " basic = " + basicModel + " cred = " + credModel + " pinsModel = " + pinsModel);

        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("user_boards/" + uid + "/boards/" + boardId, OWNER_LEVEL);
        mp.put("user_boards/" + uid + "/num", ServerValue.increment(1));
        mp.put("board_meta/" + boardId + "/data/title", basicModel.getName());
        mp.put("board_meta/" + boardId + "/data/description", basicModel.getDescription());
        mp.put("board_meta/" + boardId + "/visibility", visibilityArray[basicModel.getVisibility()]);
        mp.put("board_meta/" + boardId + "/ownerId", uid);
        mp.put("board_meta/" + boardId + "/ownerName", user.getName());
        mp.put("board_meta/" + boardId + "/time", ServerValue.TIMESTAMP);
        mp.put("board_meta/" + boardId + "/lastUpdated", ServerValue.TIMESTAMP);
        mp.put("board_cred/" + boardId, new BoardCredModel(credModel.getId(), credModel.getUsername(), credModel.getPassword()));
        mp.put("board_details/" + boardId + "/heartBeat", ServerValue.TIMESTAMP);
        mp.put("board_details/" + boardId + "/pins", pinsModel.getUsablePins());
        if(basicModel.getVisibility() == 0){
            mp.put("board_public/" + boardId, ServerValue.TIMESTAMP);
        }else{
            mp.put("board_private/" + boardId, ServerValue.TIMESTAMP);
        }
        db.updateChildren(mp, (error, ref) -> {
            loadingDialogFragment.dismiss();
            if(error == null){
                replyIntent.putExtra(BoardFormConst.TITLE, basicModel.getName());
                setResult(RESULT_OK, replyIntent);
                finish();
            }else{
                Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
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
            if(loadingDialogFragment.isVisible()){
                Log.d(TAG, "onBackPressed: doing work");
                Snackbar.make(findViewById(android.R.id.content), R.string.please_wait, Snackbar.LENGTH_SHORT).show();
            }else{
                super.onBackPressed();
            }
        }
    }
}