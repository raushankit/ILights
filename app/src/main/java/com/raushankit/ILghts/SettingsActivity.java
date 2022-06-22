package com.raushankit.ILghts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.dialogs.LoadingDialogFragment;
import com.raushankit.ILghts.dialogs.WebViewDialogFragment;
import com.raushankit.ILghts.entity.InfoType;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.fragments.settings.ChangeName;
import com.raushankit.ILghts.fragments.settings.EditPinItemFragment;
import com.raushankit.ILghts.fragments.settings.EditPinsFragment;
import com.raushankit.ILghts.fragments.settings.ManageUserFragment;
import com.raushankit.ILghts.fragments.settings.ReAuthFragment;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.ThemeData;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.utils.AnalyticsParam;
import com.raushankit.ILghts.utils.ColorGen;
import com.raushankit.ILghts.utils.ProfilePic;
import com.raushankit.ILghts.utils.UserUpdates;
import com.raushankit.ILghts.viewModel.SettingCommViewModel;
import com.raushankit.ILghts.viewModel.SettingUserViewModel;
import com.raushankit.ILghts.viewModel.SettingsFragmentViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, AppBarLayout.OnOffsetChangedListener, InstallStateUpdatedListener {
    private static final String TAG = "SETTINGS_ACTIVITY";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.4f;
    private static final long ALPHA_ANIMATIONS_DURATION = 200;
    private static final int DAYS_FOR_FLEXIBLE_UPDATE = 30;
    private static final int RC_PLAY_UPDATE = 9858922;
    private static final int PREF_PLAY_UPDATE = 9858955;
    private static final int ADMIN_THRESHOLD = 2;
    private static final String link = "https://raushankit.github.io/ILights/";

    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Pair<Boolean, Boolean> providerData;
    private UserViewModel userViewModel;
    private SettingUserViewModel settingUserViewModel;
    private SettingCommViewModel settingCommViewModel;
    private AppUpdateManager appUpdateManager;
    private WebViewDialogFragment webViewDialogFragment;
    private LoadingDialogFragment loadingDialogFragment;
    private String name = "";
    private SharedRepo sharedRepo;
    private SettingsFragmentViewModel settingsFragmentViewModel;
    private AlertDialogFragment alertDialogFragment;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Intent badAuthIntent;
    private int accessLevel;
    private Snackbar snackbar;
    private TextView toolBarTextView;
    private boolean mIsTheTitleVisible = false;
    private FirebaseAuth.AuthStateListener authListener;

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_fragment, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        webViewDialogFragment = WebViewDialogFragment.newInstance(link);
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.transparent));
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Settings Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
        sharedRepo = SharedRepo.newInstance(this);
        snackbar = Snackbar.make(findViewById(android.R.id.content), "", BaseTransientBottomBar.LENGTH_SHORT);
        badAuthIntent = new Intent(this, MainActivity.class);
        badAuthIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        db = FirebaseDatabase.getInstance().getReference();
        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) return;
            Log.w(TAG, "onCreate: null user");
            startActivity(badAuthIntent);
            userViewModel.resetRepository();
            finish();
        };
        settingsFragmentViewModel = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        settingUserViewModel = new ViewModelProvider(this).get(SettingUserViewModel.class);
        settingUserViewModel.addUserSource(userViewModel.getUserData());
        settingUserViewModel.addRoleSource(userViewModel.getRoleData());
        settingCommViewModel = new ViewModelProvider(this).get(SettingCommViewModel.class);
        providerData = getProviderData();
        alertDialogFragment = AlertDialogFragment.newInstance(R.string.confirm_action, true, true);
        alertDialogFragment.setPositiveButtonText(R.string.yes);
        alertDialogFragment.setNegativeButtonText(R.string.no);
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if (whichButton.equals(AlertDialogFragment.WhichButton.POSITIVE)) {
                if (alertDialogFragment.getActionType().equals("sign_out")) {
                    mAuth.signOut();
                } else {
                    Log.w(TAG, "onCreate: bad event");
                }
            }
            alertDialogFragment.dismiss();
        });

        shimmerFrameLayout = findViewById(R.id.settings_user_data_shimmer);
        toolBarTextView = findViewById(R.id.settings_activity_toolbar_textview);
        implementListeners();
        fillUserdata();
        AppBarLayout appBarLayout = findViewById(R.id.settings_activity_appbar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private Pair<Boolean, Boolean> getProviderData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return new Pair<>(false, false);
        for (UserInfo userInfo : user.getProviderData()) {
            boolean isProviderGoogle = userInfo.getProviderId().equals("google.com");
            if (isProviderGoogle) return new Pair<>(true, user.isEmailVerified());
        }
        return new Pair<>(false, user.isEmailVerified());
    }

    private void implementListeners() {
        settingCommViewModel.getSelectedItem().observe(this, item -> {
            Bundle bundle = new Bundle();
            String requestKey = settingCommViewModel.getRequestKey();
            if (requestKey.equals(SettingCommViewModel.DEFAULT_KEY)) return;
            switch (requestKey) {
                case "theme":
                    if (!(item instanceof ThemeData)) break;
                    ThemeData themeData = (ThemeData) item;
                    bundle.putString(AnalyticsParam.THEME_TYPE, themeData.getThemeType());
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    if (themeData.getThemeType().equals("light")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    if (themeData.getThemeType().equals("dark")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && themeData.getThemeType().equals("follow_system")) {
                        AppCompatDelegate.setDefaultNightMode(themeData.isBatterySaverOn() ? AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    break;
                case "verify_email":
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    settingsFragmentViewModel.setProfileInfo(InfoType.Keys.EMAIL_VERIFICATION,user.isEmailVerified() ? InfoType.EMAIL_VERIFIED : InfoType.EMAIL_NOT_VERIFIED);
                    boolean value = Boolean.parseBoolean(sharedRepo.getValue(SharedRefKeys.EMAIL_VERIFICATION));
                    if (!user.isEmailVerified() && !value) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(task -> {
                                    Snackbar.make(findViewById(android.R.id.content), (task.isSuccessful() ? getString(R.string.sent_verification_email) : getString(R.string.sent_no_verification_email)), BaseTransientBottomBar.LENGTH_SHORT).show();
                                    sharedRepo.insert(SharedRefKeys.EMAIL_VERIFICATION, String.valueOf(task.isSuccessful()));
                                });
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), user.isEmailVerified() ? R.string.email_already_verified : R.string.email_verification_sign_out, BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    break;
                case "sign_out":
                    alertDialogFragment.setBodyString(getString(R.string.sign_out_body_text));
                    alertDialogFragment.setActionType(requestKey);
                    if (!alertDialogFragment.isAdded())
                        alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                    break;
                case "edit_pin":
                    if (!(item instanceof EditPinInfo)) break;
                    final Fragment fragmentEdit = EditPinItemFragment.newInstance("edit", (EditPinInfo) item);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.settings_fragment, fragmentEdit)
                            .addToBackStack(null)
                            .commit();
                    break;
                case "add_pin":
                    List<String> list = new ArrayList<>();
                    for (int i = 1; i < ((List<?>) item).size(); i++) {
                        Boolean val = (Boolean) ((List<?>) item).get(i);
                        if (val) list.add(getString(R.string.add_pin_frag_item_pin_number, i));
                    }
                    if (list.isEmpty()) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.all_pins_active, BaseTransientBottomBar.LENGTH_SHORT).show();
                    } else {
                        final Fragment fragmentAdd = EditPinItemFragment.newInstance("add", list.toArray(new String[0]));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.settings_fragment, fragmentAdd)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case "delete_pin_item":
                    bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.DELETE_PIN);
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    int deletePinNum = (int) item;
                    Map<String, Object> mp1 = new LinkedHashMap<>();
                    mp1.put("control/info/" + deletePinNum, null);
                    mp1.put("control/info/" + deletePinNum, null);
                    mp1.put("control/update/" + deletePinNum, null);
                    mp1.put("control/status/" + deletePinNum, null);
                    db.updateChildren(mp1, ((error, ref) -> Snackbar.make(findViewById(android.R.id.content), (error == null ? getString(R.string.pin_name_delete_successful, deletePinNum) : getString(R.string.pin_name_delete_failure)), BaseTransientBottomBar.LENGTH_SHORT).show()));
                    break;
                case "edit_pin_item":
                    bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.EDIT_PIN);
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    EditPinInfo info = (EditPinInfo) item;
                    db.child("control/info/" + info.getPinNumber()).setValue(info.getPinInfo())
                            .addOnCompleteListener(task -> Snackbar.make(findViewById(android.R.id.content), (task.isSuccessful() ? getString(R.string.pin_name_update_successful, info.getPinNumber()) : getString(R.string.pin_name_update_failure)), BaseTransientBottomBar.LENGTH_SHORT).show());
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                case "add_pin_item":
                    if (!(item instanceof EditPinInfo)) break;
                    bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.ADD_PIN);
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    EditPinInfo info1 = (EditPinInfo) item;
                    Map<String, Object> mp = new LinkedHashMap<>();
                    mp.put("control/info/" + info1.getPinNumber(), info1.getPinInfo());
                    mp.put("control/update/" + info1.getPinNumber(), PinData.toMap(name, Objects.requireNonNull(mAuth.getUid())));
                    mp.put("control/status/" + info1.getPinNumber(), Boolean.FALSE);
                    db.updateChildren(mp, ((error, ref) -> Snackbar.make(findViewById(android.R.id.content), (error == null ? getString(R.string.pin_name_add_successful, info1.getPinNumber()) : getString(R.string.pin_name_add_failure)), BaseTransientBottomBar.LENGTH_SHORT).show()));
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                case "play_update":
                    appUpdateManager.getAppUpdateInfo().addOnSuccessListener(it1 -> {
                        if (it1.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it1.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                            try {
                                appUpdateManager.startUpdateFlowForResult(it1, AppUpdateType.FLEXIBLE, this, PREF_PLAY_UPDATE);
                            } catch (IntentSender.SendIntentException e) {
                                final String appPackageName = getPackageName(); // package name of the app
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        }
                    });
                    break;
                case "delete_user":
                    loadingDialogFragment.setTitle(R.string.deleting_user);
                    loadingDialogFragment.setMessage(R.string.delete_user_message);
                    loadingDialogFragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG);
                    bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.DELETE_USER);
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    Map<String, Object> mp2 = new LinkedHashMap<>();
                    String uid = mAuth.getUid();
                    mp2.put("users/" + uid, null);
                    mp2.put("role/" + uid, null);
                    db.updateChildren(mp2, ((error, ref) -> {
                        if (error == null) {
                            loadingDialogFragment.setMessage(R.string.please_wait);
                            Objects.requireNonNull(mAuth.getCurrentUser()).delete()
                                    .addOnFailureListener(e -> Snackbar.make(findViewById(android.R.id.content), Objects.requireNonNull(e.getMessage()), BaseTransientBottomBar.LENGTH_SHORT).show());
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }));
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                case "update_password":
                    bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.UPDATE_PASSWORD);
                    mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                    Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(String.valueOf(item))
                            .addOnCompleteListener(task -> showSnack(task.isSuccessful(), R.string.password_updated, R.string.password_update_failure, task.getException()));
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                case "privacy_policy":
                    if (!webViewDialogFragment.isAdded()) {
                        bundle.putString(FirebaseAnalytics.Param.METHOD, AnalyticsParam.PRIVACY_POLICY);
                        mFirebaseAnalytics.logEvent(AnalyticsParam.Event.SETTINGS_CHANGE, bundle);
                        webViewDialogFragment.show(getSupportFragmentManager(), "privacy_policy");
                    }
                    break;
                case "edit_analytics_state":
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled((Boolean) item);
                    if (Boolean.FALSE.equals(item)) {
                        sharedRepo.insertBoolean(SharedRefKeys.SHOW_ANALYTICS_DIALOG, true);
                        sharedRepo.insertLong(SharedRefKeys.PREV_SHOWN_ANALYTICS_DIALOG, Calendar.getInstance().getTimeInMillis());
                    }
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.settings_snack_message), BaseTransientBottomBar.LENGTH_SHORT).show();
                    break;
                default:
                    Log.w(TAG, "implementListeners: clear events");
                    return;
            }
            settingCommViewModel.selectItem(SettingCommViewModel.DEFAULT_KEY, null);
        });
    }

    private void showSnack(boolean isSuccessful, @StringRes int successMessage, @StringRes int failureMessage, @Nullable Exception exception) {
        if (isSuccessful) snackbar.setText(successMessage);
        else {
            if (exception == null || exception.getMessage() == null)
                snackbar.setText(failureMessage);
            else snackbar.setText(exception.getMessage());
        }
        snackbar.show();
    }

    private void fillUserdata() {
        ConstraintLayout userLayout = findViewById(R.id.settings_user_data);
        ProfilePic pic = userLayout.findViewById(R.id.user_info_profile_pic);
        TextView nameView = userLayout.findViewById(R.id.user_info_name);
        TextView emailView = userLayout.findViewById(R.id.user_info_email);
        TextView roleView = userLayout.findViewById(R.id.user_info_access_level);
        String[] roleType = getResources().getStringArray(R.array.user_types);

        pic.setBackGroundCol(ColorGen.getRandomDarkColor(0.7f));

        settingUserViewModel.getUserData().observe(this, settingUserData -> {
            Log.i(TAG, "fillUserdata: settingUserData " + settingUserData);
            name = settingUserData.getUser().getName();
            toolBarTextView.setText(name);
            nameView.setText(name);
            pic.setName(name);
            emailView.setText(settingUserData.getUser().getEmail());
            accessLevel = Math.max(0, Math.min(roleType.length, settingUserData.getRole().getAccessLevel()));
            settingsFragmentViewModel.setProfileInfo(InfoType.Keys.ADMIN,accessLevel >= ADMIN_THRESHOLD ? InfoType.ADMIN_VISIBILITY : InfoType.ADMIN_INVISIBILITY);
            roleView.setText(getString(R.string.access_level_place_holder, roleType[accessLevel]));
            userLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            settingsFragmentViewModel.setProfileInfo(InfoType.Keys.LOGIN_METHOD, providerData.first ? InfoType.LOGIN_METHOD_GOOGLE : InfoType.LOGIN_METHOD_EMAIL);
            settingsFragmentViewModel.setProfileInfo(InfoType.Keys.EMAIL_VERIFICATION, providerData.second ? InfoType.EMAIL_VERIFIED : InfoType.EMAIL_NOT_VERIFIED);
            settingsFragmentViewModel.setProfileInfo(InfoType.Keys.PROFILE, InfoType.PROFILE_VISIBILITY);
            sharedRepo.insert(SharedRefKeys.EMAIL_VERIFICATION, String.valueOf(false));
        });
        UserUpdates u = new UserUpdates();
        appUpdateManager.getAppUpdateInfo().addOnCompleteListener(task -> u.setUpdateLog(TAG, task));
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                settingsFragmentViewModel.setVersionInfo(new VersionInfo("", appUpdateInfo.availableVersionCode()));
                if (appUpdateInfo.clientVersionStalenessDays() != null
                        && Objects.requireNonNull(appUpdateInfo.clientVersionStalenessDays()) >= DAYS_FOR_FLEXIBLE_UPDATE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                        && isUpdateNotificationAllowed()) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, RC_PLAY_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.w(TAG, "fillUserdata: no update available");
            }
        });
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        switch (pref.getKey()) {
            case "change_name":
                args.putString("prev_name", name);
                args.putString("user_uid", mAuth.getUid());
                pref.setFragment(ChangeName.class.getName());
                break;
            case "manage_users":
                args.putInt("role", accessLevel);
                pref.setFragment(ManageUserFragment.class.getName());
                break;
            case "manage_pins":
                args.putString("user_name", name);
                args.putString("user_uid", mAuth.getUid());
                pref.setFragment(EditPinsFragment.class.getName());
                break;
            case "change_password":
                args.putString("action", "change_password");
                pref.setFragment(ReAuthFragment.class.getName());
                break;
            case "delete_account":
                args.putString("action", "delete_account");
                pref.setFragment(ReAuthFragment.class.getName());
                break;
            default:
                Log.w(TAG, "onPreferenceStartFragment: no fragment as such: ");
        }
        if (pref.getFragment() == null) return false;
        final Fragment fragment = getSupportFragmentManager()
                .getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        getSupportFragmentManager().setFragmentResultListener("request", this, (requestKey, result) -> {
            if (requestKey.equals("request")) {
                Snackbar.make(findViewById(android.R.id.content), result.getString("result"), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(authListener);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(it -> {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && (it.installStatus() == InstallStatus.DOWNLOADING || it.installStatus() == InstallStatus.PENDING)) {
                appUpdateManager.registerListener(this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        appUpdateManager.unregisterListener(this);
        mAuth.removeAuthStateListener(authListener);
        settingUserViewModel.removeSource(userViewModel.getUserData());
        settingUserViewModel.removeSource(userViewModel.getRoleData());
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float factor = Math.abs(verticalOffset) / (maxScroll * 1f);
        handleToolbarTitleVisibility(factor);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolBarTextView, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolBarTextView, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content), R.string.update_downloaded, Snackbar.LENGTH_INDEFINITE);
        snackbar1.setAction(R.string.restart, view -> appUpdateManager.completeUpdate());
        snackbar1.setActionTextColor(getResources().getColor(R.color.scarlet_red, getTheme()));
        snackbar1.show();
    }

    private boolean isUpdateNotificationAllowed() {
        long lastTime = sharedRepo.getLongValue(SharedRefKeys.NOTIFY_UPDATE, -1);
        if (lastTime == -1) return true;
        return Calendar.getInstance().getTimeInMillis() - lastTime >= DAYS_FOR_FLEXIBLE_UPDATE * 24 * 3600000L;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PLAY_UPDATE || requestCode == PREF_PLAY_UPDATE) {
            switch (resultCode) {
                case RESULT_OK:
                    appUpdateManager.registerListener(this);
                    break;
                case RESULT_CANCELED:
                    if (requestCode == RC_PLAY_UPDATE)
                        sharedRepo.insertLong(SharedRefKeys.NOTIFY_UPDATE, Calendar.getInstance().getTimeInMillis());
                    break;
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                    snackbar.setText(R.string.failed_to_update);
                    snackbar.show();
                    break;
                default:
                    Log.w(TAG, "onActivityResult: uncaptured result code: " + resultCode);
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onStateUpdate(@NonNull InstallState installState) {
        Snackbar snackbarUpdate = Snackbar.make(findViewById(android.R.id.content), "", BaseTransientBottomBar.LENGTH_SHORT);
        switch (installState.installStatus()) {
            case InstallStatus.DOWNLOADED:
                popupSnackbarForCompleteUpdate();
                appUpdateManager.unregisterListener(this);
                break;
            case InstallStatus.FAILED:
                snackbarUpdate.setText(R.string.failed_to_update);
                snackbarUpdate.show();
                break;
            case InstallStatus.INSTALLED:
                snackbarUpdate.setText(R.string.successfully_updated);
                snackbarUpdate.show();
                break;
            default:
                Log.w(TAG, "onStateUpdate: event type = " + installState.installStatus());
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private SettingCommViewModel settingCommViewModelFrag;
        private ListPreference themePreference;
        private CheckBoxPreference batterySaverPreference;
        private PreferenceCategory profileCategory;
        private PreferenceCategory adminCategory;
        private Preference verifyEmailPreference;
        private Preference updatePreference;
        private Preference changePWPreference;

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            settingCommViewModelFrag = new ViewModelProvider(requireActivity()).get(SettingCommViewModel.class);
            SettingsFragmentViewModel sFViewModel = new ViewModelProvider(requireActivity()).get(SettingsFragmentViewModel.class);
            sFViewModel.getLiveProfileInfo().observe(getViewLifecycleOwner(), infoType -> {
                switch (infoType) {
                    case PROFILE_VISIBILITY:
                        if (profileCategory != null) profileCategory.setEnabled(true);
                        break;
                    case EMAIL_NOT_VERIFIED:
                    case EMAIL_VERIFIED:
                        if (verifyEmailPreference != null)
                            verifyEmailPreference.setSummary(infoType.equals(InfoType.EMAIL_VERIFIED) ? R.string.email_verified : R.string.email_not_verified);
                        break;
                    case ADMIN_VISIBILITY:
                    case ADMIN_INVISIBILITY:
                        if (adminCategory != null)
                            adminCategory.setVisible(infoType.equals(InfoType.ADMIN_VISIBILITY));
                        break;
                    case LOGIN_METHOD_GOOGLE:
                    case LOGIN_METHOD_EMAIL:
                        if (changePWPreference != null)
                            changePWPreference.setVisible(infoType.equals(InfoType.LOGIN_METHOD_EMAIL));
                        break;
                    default:
                        Log.w(TAG, "setProfileCategory: infoType = " + infoType);
                }
            });
            sFViewModel.getLiveVersionInfo().observe(getViewLifecycleOwner(), vInfo -> {
                if (updatePreference == null) return;
                updatePreference.setVisible(true);
                updatePreference.setSummary(getString(R.string.update_summary, vInfo.getVersionCode()));
            });
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            themePreference = findPreference("theme");
            batterySaverPreference = findPreference("battery_saver_theme");
            profileCategory = findPreference("profile_category");
            adminCategory = findPreference("admin_category");
            verifyEmailPreference = findPreference("verify_email");
            Preference versionNamePreference = findPreference("version_name");
            Preference privacyPolicyPreference = findPreference("privacy_policy");
            updatePreference = findPreference("update_available");
            Preference signOutPreference = findPreference("sign_out");
            changePWPreference = findPreference("change_password");
            CheckBoxPreference checkBoxPreference = findPreference("send_statistics");

            if (themePreference != null) {
                themePreference.setOnPreferenceChangeListener(this);
            }
            if (batterySaverPreference != null) {
                String themeValue = (themePreference == null || themePreference.getValue() == null ? "null" : themePreference.getValue());
                batterySaverPreference.setEnabled(themeValue.equals("follow_system"));
                batterySaverPreference.setSummary(themeValue.equals("follow_system") ? R.string.battery_saver_summary : R.string.battery_saver_disabled_summary);
                batterySaverPreference.setOnPreferenceChangeListener(this);
            }
            if (privacyPolicyPreference != null) {
                privacyPolicyPreference.setOnPreferenceClickListener(this);
            }
            if (verifyEmailPreference != null) {
                verifyEmailPreference.setOnPreferenceClickListener(this);
            }
            if (checkBoxPreference != null) {
                checkBoxPreference.setOnPreferenceChangeListener(this);
            }
            if (signOutPreference != null) {
                signOutPreference.setOnPreferenceClickListener(this);
            }
            if (updatePreference != null) {
                updatePreference.setOnPreferenceClickListener(this);
            }
            if (versionNamePreference != null) {
                versionNamePreference.setSummary(BuildConfig.VERSION_NAME);
                versionNamePreference.setOnPreferenceClickListener(this);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case "theme":
                    settingCommViewModelFrag.selectItem("theme", new ThemeData((String) newValue, batterySaverPreference.isChecked()));
                    batterySaverPreference.setEnabled(newValue.equals("follow_system"));
                    batterySaverPreference.setSummary(newValue.equals("follow_system") ? R.string.battery_saver_summary : R.string.battery_saver_disabled_summary);
                    return true;
                case "send_statistics":
                    settingCommViewModelFrag.selectItem("edit_analytics_state", newValue);
                    return true;
                case "battery_saver_theme":
                    settingCommViewModelFrag.selectItem("theme", new ThemeData(themePreference.getValue(), (Boolean) newValue));
                    return true;
            }
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "verify_email":
                    settingCommViewModelFrag.selectItem("verify_email", null);
                    return true;
                case "sign_out":
                    settingCommViewModelFrag.selectItem("sign_out", null);
                    return true;
                case "update_available":
                    settingCommViewModelFrag.selectItem("play_update", null);
                    return true;
                case "privacy_policy":
                    settingCommViewModelFrag.selectItem("privacy_policy", null);
                    return true;
                default:
                    Log.w(TAG, "onPreferenceClick: unknown click event");
                    break;
            }
            return false;
        }
    }
}