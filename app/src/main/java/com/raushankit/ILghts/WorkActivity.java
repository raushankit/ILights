package com.raushankit.ILghts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.raushankit.ILghts.entity.ControllerFragActions;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.fragments.ControllerFragment;
import com.raushankit.ILghts.fragments.ForgotPasswordFragment;
import com.raushankit.ILghts.fragments.LoginFragment;
import com.raushankit.ILghts.fragments.SignUpFragment;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.FragViewModel;

public class WorkActivity extends AppCompatActivity implements InstallStateUpdatedListener {

    private static final String TAG = "WorkActivity";
    private CallBack<PageKeys> changeFragment;
    private Intent signOutIntent;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        signOutIntent = new Intent(this, MainActivity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        FragViewModel fragViewModel = new ViewModelProvider(this).get(FragViewModel.class);
        fragViewModel.getSelectedItem().observe(this, item ->{
            if(item.equals(ControllerFragActions.OPEN_SETTINGS)) {
                startActivity(settingsIntent);
            }else if(item.equals(ControllerFragActions.BLOCK_EVENT)){
                startActivity(signOutIntent);
                finish();
            }else{
                Log.d(TAG, "onCreate: unknown event");
            }
        });
        changeFragment = value -> replaceFragment(value.name());
        switchFrags();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.registerListener(this);
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content),R.string.update_downloaded, Snackbar.LENGTH_INDEFINITE);
        snackbar1.setAction(R.string.restart, view -> appUpdateManager.completeUpdate());
        snackbar1.setActionTextColor(getResources().getColor(R.color.scarlet_red, getTheme()));
        snackbar1.show();
    }

    private void switchFrags(){
        Intent receiveIntent = getIntent();
        if(receiveIntent == null) return;
        String page = receiveIntent.getStringExtra(PageKeys.WHICH_PAGE.name());
        if(page == null) return;
        replaceFragment(page);
    }

    private void replaceFragment(String id){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        CallBack<Integer> statusBarColor = value -> window.setStatusBarColor(getColor(value));
        FragmentManager fm = getSupportFragmentManager();
        Intent intent = getIntent();
        FragmentTransaction ft = fm.beginTransaction();
        if(id.equals(PageKeys.SIGN_UP_PAGE.name())){
            if(intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new SignUpFragment(
                    statusBarColor, changeFragment
            ));
            ft.commit();
        }
        else if(id.equals(PageKeys.LOGIN_PAGE.name()) || id.equals(PageKeys.GOOGLE_LOGIN_EVENT.name())){
            if(intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new LoginFragment(
                    statusBarColor,changeFragment,
                    id.equals(PageKeys.GOOGLE_LOGIN_EVENT.name())
            ));
            ft.commit();
        }
        else if(id.equals(PageKeys.FORGOT_PASSWORD_PAGE.name())){
            if(intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new ForgotPasswordFragment(
                    statusBarColor, changeFragment
            )).addToBackStack(null)
                    .commit();
        }
        else if(id.equals(PageKeys.CONTROLLER_PAGE.name())){
            if(intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            window.setStatusBarColor(getColor(R.color.controller_title_background));
            ft.replace(R.id.work_main_frame, ControllerFragment.newInstance())
                    .commit();
        }else{
            Log.d(TAG, "replaceFragment: invalid Fragment action");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if(intent != null){
            String page = intent.getStringExtra(PageKeys.WHICH_PAGE.name());
            if(page != null){
                if(page.equals(PageKeys.LOGIN_PAGE.name()) || page.equals(PageKeys.SIGN_UP_PAGE.name())){
                    startActivity(signOutIntent);
                    finish();
                    return;
                }
                if(page.equals(PageKeys.CONTROLLER_PAGE.name())){
                    finishAffinity();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onStateUpdate(@NonNull InstallState installState) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),R.string.unknown_error, BaseTransientBottomBar.LENGTH_SHORT);
        switch (installState.installStatus()) {
            case InstallStatus.DOWNLOADED:
                popupSnackbarForCompleteUpdate();
                break;
            case InstallStatus.FAILED:
                snackbar.setText(R.string.failed_to_update);
                snackbar.show();
                break;
            case InstallStatus.INSTALLED:
                snackbar.setText(R.string.successfully_updated);
                snackbar.show();
                break;
            default:
                Log.w(TAG, "onStateUpdate: event type = " + installState.installStatus());
        }
    }

    @Override
    protected void onDestroy() {
        appUpdateManager.unregisterListener(this);
        super.onDestroy();
    }
}