package com.raushankit.ILghts;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.dialogs.ConsentDialogFragment;
import com.raushankit.ILghts.dialogs.WebViewDialogFragment;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.entity.UpdateType;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.observer.UpdateTypeLiveData;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.utils.AnalyticsParam;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.UserUpdates;
import com.raushankit.ILghts.viewModel.SplashViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String link = "https://raushankit.github.io/ILights/";
    private static final long SLOW_INTERNET_TIMEOUT = 10000;
    private static final int RC_PLAY_UPDATE = 98922;
    private Handler mHandler;
    private Runnable runnable;
    private Snackbar snackbar;
    private Intent intent;
    private Intent blockedIntent;
    private AppUpdateManager appUpdateManager;
    private SplashViewModel splashViewModel;
    private boolean updateStartedHere = false;
    private AlertDialogFragment alertDialogFragment;
    private boolean isUpdateAvailable = false;
    private WebViewDialogFragment webViewDialogFragment;
    private FirebaseAnalytics mAnalytics;
    private SharedPreferences sharedPreferences;
    private SharedRepo sharedRepo;
    private ConsentDialogFragment consentDialogFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase.getApplicationContext());
        sharedRepo = SharedRepo.newInstance(newBase.getApplicationContext());
        boolean isv29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        String themeType = sharedPreferences.getString("theme", isv29 ? "follow_system" : "light");
        if (themeType.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (themeType.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (isv29 && themeType.equals("follow_system")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String checkedIndex = sharedRepo.getValue(SharedRefKeys.THEME_INDEX);
        int themeIndex = Integer.parseInt(SharedRefKeys.DEFAULT_VALUE.name().equals(checkedIndex) ? "0": checkedIndex);
        setTheme(StringUtils.getTheme(themeIndex));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAnalytics = FirebaseAnalytics.getInstance(this);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        ((BaseApp)getApplication()).setThemeIndex(themeIndex);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splash_screen_bg_end));
        if (getColor(R.color.splash_screen_btn_text) == Color.WHITE) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        intent = new Intent(this, WorkActivity.class);
        blockedIntent = new Intent(this, SettingsActivity.class);
        SharedRepo sharedRepo = SharedRepo.newInstance(this);
        alertDialogFragment = AlertDialogFragment.newInstance(R.string.forced_update, true, true);
        alertDialogFragment.setPositiveButtonText(R.string.update);
        alertDialogFragment.setBodyString(getString(R.string.forced_update_message));
        alertDialogFragment.setNegativeButtonText(R.string.exit);
        webViewDialogFragment = WebViewDialogFragment.newInstance(link);
        askAgainForUpdate();
        consentDialogFragment = ConsentDialogFragment.newInstance(true, false);
        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_network_detected), BaseTransientBottomBar.LENGTH_LONG);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        mHandler = new Handler(Looper.getMainLooper());
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        mAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
        consentDialogFragment.addOnActionClickListener(action -> {
            switch (action) {
                case AGREE:
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("send_statistics", true);
                    editor.apply();
                    mAnalytics.setAnalyticsCollectionEnabled(true);
                    sharedRepo.insert(SharedRefKeys.FIRST_OPEN, Boolean.TRUE.toString());
                    consentDialogFragment.dismiss();
                    startActivity(intent);
                    finish();
                    break;
                case DISAGREE:
                    mAnalytics.setAnalyticsCollectionEnabled(false);
                    consentDialogFragment.dismiss();
                    sharedRepo.insert(SharedRefKeys.FIRST_OPEN, Boolean.TRUE.toString());
                    sharedRepo.insertLong(SharedRefKeys.PREV_SHOWN_ANALYTICS_DIALOG, Calendar.getInstance().getTimeInMillis());
                    startActivity(intent);
                    finish();
                    break;
                case POLICY:
                    if (!webViewDialogFragment.isAdded())
                        webViewDialogFragment.show(getSupportFragmentManager(), "privacy_policy");
                    break;
                default:
                    Log.w(TAG, "onCreate: bad click event");
            }
        });

        LottieAnimationView spv = findViewById(R.id.splash_screen_lottie);
        LottieAnimationView networkLoader = findViewById(R.id.splash_screen_network_lottie);
        networkLoader.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                frameInfo -> new PorterDuffColorFilter(MaterialColors.getColor(MainActivity.this, R.attr.splashScreenColor, getColor(R.color.splash_screen_btn))
                        , PorterDuff.Mode.SRC_ATOP)
        );
        TextView spText = findViewById(R.id.splash_main_text);
        TextView privacyText = findViewById(R.id.splash_screen_privacy_policy);
        setTextViewHTML(privacyText, getString(R.string.splash_page_policy, link));
        TextView versionText = findViewById(R.id.splash_screen_version_info);
        versionText.setText(getString(R.string.splash_page_version_placeholder, BuildConfig.VERSION_NAME));
        TextView helperText = findViewById(R.id.splash_screen_sign_in_btn_helper_text);
        MaterialButton registerBtn = findViewById(R.id.splash_screen_register_btn);
        MaterialButton signInBtn = findViewById(R.id.splash_screen_sign_in_btn);
        registerBtn.setVisibility(View.GONE);
        signInBtn.setVisibility(View.GONE);
        helperText.setVisibility(View.GONE);
        privacyText.setVisibility(View.GONE);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);

        Animation zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        spText.startAnimation(zoomOutAnimation);
        spv.setMinAndMaxProgress(0.0f, 0.6f);

        runnable = () -> {
            if (user != null && !isUpdateAvailable) snackbar.show();
        };

        UserUpdates u = new UserUpdates();
        appUpdateManager.getAppUpdateInfo().addOnCompleteListener(task -> {
            u.setUpdateLog(TAG, task);
            if (task.isSuccessful()) {
                AppUpdateInfo appUpdateInfo = task.getResult();
                splashViewModel.setAppUpdateInfo(appUpdateInfo);
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    splashViewModel.addSource(new UpdateTypeLiveData(appUpdateInfo.availableVersionCode()));
                    isUpdateAvailable = true;
                } else {
                    splashViewModel.setVersionFlag(true);
                    if (user == null) {
                        registerBtn.setVisibility(View.VISIBLE);
                        signInBtn.setVisibility(View.VISIBLE);
                        helperText.setVisibility(View.VISIBLE);
                        privacyText.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                splashViewModel.setVersionFlag(true);
                if (user == null) {
                    registerBtn.setVisibility(View.VISIBLE);
                    signInBtn.setVisibility(View.VISIBLE);
                    helperText.setVisibility(View.VISIBLE);
                    privacyText.setVisibility(View.VISIBLE);
                }
                Log.w(TAG, "appUpdateInfoTask: " + (task.getException() != null && task.getException().getMessage() != null ? task.getException().getMessage() : "failed to fetch update"));
            }
        });

        registerBtn.setOnClickListener(v -> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.SIGN_UP_PAGE.name());
            Log.w(TAG, "onCreate: first_time register = " + sharedRepo.getValue(SharedRefKeys.FIRST_OPEN));
            if (sharedRepo.getValue(SharedRefKeys.FIRST_OPEN).equals(SharedRefKeys.DEFAULT_VALUE.name())) {
                consentDialogFragment.show(getSupportFragmentManager(), ConsentDialogFragment.TAG);
            } else {
                startActivity(intent);
                finish();
            }
        });
        signInBtn.setOnClickListener(v -> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.LOGIN_PAGE.name());
            if (sharedRepo.getValue(SharedRefKeys.FIRST_OPEN).equals(SharedRefKeys.DEFAULT_VALUE.name())) {
                consentDialogFragment.show(getSupportFragmentManager(), ConsentDialogFragment.TAG);
            } else {
                startActivity(intent);
                finish();
            }
        });

        String isAuthSuccess = sharedRepo.getValue(SharedRefKeys.AUTH_SUCCESSFUL);
        networkLoader.setVisibility(user == null ? View.GONE : View.VISIBLE);
        mHandler.postDelayed(runnable, SLOW_INTERNET_TIMEOUT);
        splashViewModel.setRoleFlag(user == null);
        if (user != null && Boolean.parseBoolean(isAuthSuccess)) {
            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            splashViewModel.addSource(userViewModel.getRoleData());
        } else {
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsParam.BAD_USER, "user is created without database event");
                mAnalytics.logEvent(AnalyticsParam.Event.UNEXPECTED_EVENT, bundle);
                FirebaseAuth.getInstance().signOut();
                Log.w(TAG, "onCreate: app in bad state");
            }
        }

        splashViewModel.getData().observe(this, splashData -> {
            if (isUpdateAvailable) {
                if (splashData.getUpdatePriority() != null && splashData.getUpdatePriority().getPriority() >= UpdateType.FORCED) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(splashViewModel.getAppUpdateInfo(), AppUpdateType.IMMEDIATE, this, RC_PLAY_UPDATE);
                        updateStartedHere = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    isUpdateAvailable = false;
                    if (user != null) {
                        normalRoleFlow(splashData.getRole());
                    } else {
                        registerBtn.setVisibility(View.VISIBLE);
                        signInBtn.setVisibility(View.VISIBLE);
                        helperText.setVisibility(View.VISIBLE);
                        privacyText.setVisibility(View.VISIBLE);
                    }
                }
                return;
            }
            normalRoleFlow(splashData.getRole());
        });
    }

    private void normalRoleFlow(Role r1) {
        if (r1 != null) {
            if (r1.getAccessLevel() > 0) {
                startActivity(new Intent(this, BoardActivity.class));
                finish();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsParam.BLOCKED_USER, "user is blocked");
                mAnalytics.logEvent(AnalyticsParam.Event.UNEXPECTED_EVENT, bundle);
                startActivity(blockedIntent);
            }
        } else {
            Log.w(TAG, "onCreate: role data is null");
            Bundle bundle = new Bundle();
            bundle.putString(AnalyticsParam.BAD_USER, "user is created without role data in database");
            mAnalytics.logEvent(TAG, bundle);
            FirebaseAuth.getInstance().signOut();
        }
        finish();
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (webViewDialogFragment.isAdded()) return;
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsParam.BUTTON_CLICKED, "viewing privacy policy");
                mAnalytics.logEvent(AnalyticsParam.Event.VIEW_POLICY, bundle);
                webViewDialogFragment.show(getSupportFragmentManager(), "privacy_policy");
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void askAgainForUpdate() {
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if (whichButton == AlertDialogFragment.WhichButton.POSITIVE) {
                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo1 -> {
                    if (appUpdateInfo1.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo1.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo1, AppUpdateType.IMMEDIATE, this, RC_PLAY_UPDATE);
                            updateStartedHere = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        splashViewModel.setVersionFlag(true);
                    }
                });
            } else if (whichButton == AlertDialogFragment.WhichButton.NEGATIVE) {
                finish();
            } else {
                Log.w(TAG, "askAgainForUpdate: unknown event");
            }
            alertDialogFragment.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PLAY_UPDATE) {
            switch (resultCode) {
                case RESULT_OK:
                    updateStartedHere = true;
                    break;
                case RESULT_CANCELED:
                    updateStartedHere = false;
                    alertDialogFragment.setPositiveButtonText(R.string.update);
                    alertDialogFragment.setBodyString(getString(R.string.forced_update_message));
                    alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                    break;
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                    updateStartedHere = false;
                    alertDialogFragment.setPositiveButtonText(R.string.retry);
                    alertDialogFragment.setBodyString(getString(R.string.in_app_update_failed));
                    alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                    break;
                default:
                    Log.w(TAG, "onActivityResult: un-captured result code: " + resultCode);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(it -> {
            if (updateStartedHere && it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, RC_PLAY_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(runnable);
        Log.w(TAG, "onDestroy: removing callback from handler");
        super.onDestroy();
    }
}