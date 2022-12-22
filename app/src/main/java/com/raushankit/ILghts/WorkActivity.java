package com.raushankit.ILghts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.raushankit.ILghts.dialogs.ConsentDialogFragment;
import com.raushankit.ILghts.entity.ControllerFragActions;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.fragments.ControllerFragment;
import com.raushankit.ILghts.fragments.ForgotPasswordFragment;
import com.raushankit.ILghts.fragments.LoginFragment;
import com.raushankit.ILghts.fragments.SignUpFragment;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.FragViewModel;

import java.util.Calendar;

public class WorkActivity extends AppCompatActivity implements InstallStateUpdatedListener {

    private static final String TAG = "WorkActivity";
    private static final long CONSENT_DELAY = 15 * 24 * 3600000L;
    private CallBack<PageKeys> changeFragment;
    private Intent signOutIntent;
    private AppUpdateManager appUpdateManager;
    private SharedPreferences sharedPreferences;
    private SharedRepo sharedRepo;
    private ConsentDialogFragment consentDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedRepo = SharedRepo.newInstance(this);
        consentDialogFragment = ConsentDialogFragment.newInstance(false, true);
        signOutIntent = new Intent(this, MainActivity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        FragViewModel fragViewModel = new ViewModelProvider(this).get(FragViewModel.class);
        fragViewModel.getSelectedItem().observe(this, item -> {
            if (item.equals(ControllerFragActions.OPEN_SETTINGS)) {
                startActivity(settingsIntent);
            } else if (item.equals(ControllerFragActions.BLOCK_EVENT)) {
                startActivity(signOutIntent);
                finish();
            } else {
                Log.d(TAG, "onCreate: unknown event");
            }
        });
        consentDialogFragment.addOnActionClickListener(action -> {
            switch (action) {
                case AGREE:
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("send_statistics", true);
                    editor.apply();
                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
                    sharedRepo.insertBoolean(SharedRefKeys.SHOW_ANALYTICS_DIALOG, true);
                    consentDialogFragment.dismiss();
                    break;
                case DISAGREE:
                    sharedRepo.insertLong(SharedRefKeys.PREV_SHOWN_ANALYTICS_DIALOG, Calendar.getInstance().getTimeInMillis());
                    sharedRepo.insertBoolean(SharedRefKeys.SHOW_ANALYTICS_DIALOG, !consentDialogFragment.isChecked());
                    consentDialogFragment.dismiss();
                    break;
                default:
                    Log.w(TAG, "onCreate: un-captured event");
            }
        });
        if (!sharedPreferences.getBoolean("send_statistics", false)
                && sharedRepo.getBooleanValue(SharedRefKeys.SHOW_ANALYTICS_DIALOG, true)
                && canShowConsentDialog(sharedRepo.getLongValue(SharedRefKeys.PREV_SHOWN_ANALYTICS_DIALOG, -1))) {
            consentDialogFragment.show(getSupportFragmentManager(), ConsentDialogFragment.TAG);
        }
        changeFragment = value -> {
            changeStatusBarColor(value.name());
            replaceFragment(value.name());
        };
        switchFrags(savedInstanceState != null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(ap1 -> {
            if (ap1.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
            if (ap1.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && (ap1.installStatus() == InstallStatus.DOWNLOADING || ap1.installStatus() == InstallStatus.PENDING)) {
                appUpdateManager.registerListener(this);
            }
        });
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content), R.string.update_downloaded, Snackbar.LENGTH_INDEFINITE);
        snackbar1.setAction(R.string.restart, view -> appUpdateManager.completeUpdate());
        snackbar1.setActionTextColor(getResources().getColor(R.color.scarlet_red, getTheme()));
        snackbar1.show();
    }

    private void changeStatusBarColor(String page) {
        if (page == null) return;
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (page.equals(PageKeys.SIGN_UP_PAGE.name()) || page.equals(PageKeys.LOGIN_PAGE.name()) || page.equals(PageKeys.GOOGLE_LOGIN_EVENT.name()) || page.equals(PageKeys.FORGOT_PASSWORD_PAGE.name())) {
            window.setStatusBarColor(getColor(R.color.splash_screen_bg_end));
        } else if (page.equals(PageKeys.CONTROLLER_PAGE.name())) {
            window.setStatusBarColor(getColor(R.color.controller_title_background));
        } else {
            Log.w(TAG, "switchFrags: no fragment");
        }
    }

    private void switchFrags(boolean isConfigChanged) {
        Intent receiveIntent = getIntent();
        if (receiveIntent == null) return;
        String page = receiveIntent.getStringExtra(PageKeys.WHICH_PAGE.name());
        if (page == null) return;
        changeStatusBarColor(page);
        if (!isConfigChanged) replaceFragment(page);
    }

    private void replaceFragment(String id) {
        FragmentManager fm = getSupportFragmentManager();
        Intent intent = getIntent();
        FragmentTransaction ft = fm.beginTransaction();
        Log.i(TAG, "replaceFragment: " + id);
        if (id.equals(PageKeys.SIGN_UP_PAGE.name())) {
            if (intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new SignUpFragment(changeFragment)).commit();
        } else if (id.equals(PageKeys.LOGIN_PAGE.name()) || id.equals(PageKeys.GOOGLE_LOGIN_EVENT.name())) {
            if (intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new LoginFragment(changeFragment, id.equals(PageKeys.GOOGLE_LOGIN_EVENT.name()))).commit();
        } else if (id.equals(PageKeys.FORGOT_PASSWORD_PAGE.name())) {
            if (intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            ft.replace(R.id.work_main_frame, new ForgotPasswordFragment(changeFragment)).addToBackStack(null).commit();
        } else if (id.equals(PageKeys.CONTROLLER_PAGE.name())) {
            startActivity(new Intent(this, BoardActivity.class));
            finish();
        } else {
            Log.d(TAG, "replaceFragment: invalid Fragment action");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent != null) {
            String page = intent.getStringExtra(PageKeys.WHICH_PAGE.name());
            if (page != null) {
                if (page.equals(PageKeys.LOGIN_PAGE.name()) || page.equals(PageKeys.SIGN_UP_PAGE.name())) {
                    startActivity(signOutIntent);
                    finish();
                    return;
                } else if (page.equals(PageKeys.FORGOT_PASSWORD_PAGE.name())) {
                    intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.LOGIN_PAGE.name());
                } else if (page.equals(PageKeys.CONTROLLER_PAGE.name())) {
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
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.unknown_error, BaseTransientBottomBar.LENGTH_SHORT);
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
                appUpdateManager.unregisterListener(this);
                break;
            default:
                Log.w(TAG, "onStateUpdate: event type = " + installState.installStatus());
        }
    }

    private boolean canShowConsentDialog(long prevShown) {
        if (prevShown == -1) return true;
        return prevShown + CONSENT_DELAY <= Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onDestroy() {
        appUpdateManager.unregisterListener(this);
        super.onDestroy();
    }
}