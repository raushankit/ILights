package com.raushankit.ILghts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.dialogs.ConsentDialogFragment;
import com.raushankit.ILghts.dialogs.WebViewDialogFragment;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.utils.AnalyticsParam;
import com.raushankit.ILghts.viewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String link = "https://raushankit.github.io/ILights/";
    private Handler mHandler;
    private Runnable runnable;
    private Snackbar snackbar;
    private WebViewDialogFragment webViewDialogFragment;
    private FirebaseAnalytics mAnalytics;
    private SharedPreferences sharedPreferences;
    private ConsentDialogFragment consentDialogFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] themeEntries = getResources().getStringArray(R.array.theme_values);
        String themeType = sharedPreferences.getString("theme", themeEntries[0]);
        if(themeType.equals(themeEntries[1])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(themeType.equals(themeEntries[2])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(themeType.equals(themeEntries[0])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAnalytics = FirebaseAnalytics.getInstance(this);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splash_screen_bg_end));
        Intent intent = new Intent(this, WorkActivity.class);
        Intent blockedIntent = new Intent(this, SettingsActivity.class);
        SharedRepo sharedRepo = SharedRepo.newInstance(this);
        webViewDialogFragment = WebViewDialogFragment.newInstance();
        webViewDialogFragment.setUrl(link);
        consentDialogFragment = ConsentDialogFragment.newInstance();
        snackbar = Snackbar.make(findViewById(android.R.id.content),getString(R.string.no_network_detected), BaseTransientBottomBar.LENGTH_LONG);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mHandler = new Handler();
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        mAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
        consentDialogFragment.addOnActionClickListener(action -> {
            switch (action){
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
                    startActivity(intent);
                    finish();
                    break;
                case POLICY:
                    webViewDialogFragment.show(getSupportFragmentManager(), "privacy_policy");
                    break;
                default:
                    Log.w(TAG, "onCreate: bad click event");
            }

        });

        LottieAnimationView spv = findViewById(R.id.splash_screen_lottie);
        LottieAnimationView networkLoader = findViewById(R.id.splash_screen_network_lottie);
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
        spv.setMinAndMaxProgress(0.0f,0.6f);

        runnable = () -> {
            if(user == null){
                registerBtn.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.VISIBLE);
                helperText.setVisibility(View.VISIBLE);
                privacyText.setVisibility(View.VISIBLE);
            }else{
                snackbar.show();
            }
        };

        registerBtn.setOnClickListener(v-> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.SIGN_UP_PAGE.name());
            if(sharedRepo.getValue(SharedRefKeys.FIRST_OPEN).equals(SharedRefKeys.DEFAULT_VALUE.name())){
                consentDialogFragment.show(getSupportFragmentManager(), ConsentDialogFragment.TAG);
            }else{
                startActivity(intent);
                finish();
            }
        });
        signInBtn.setOnClickListener(v-> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.LOGIN_PAGE.name());
            if(sharedRepo.getValue(SharedRefKeys.FIRST_OPEN).equals(SharedRefKeys.DEFAULT_VALUE.name())){
                consentDialogFragment.show(getSupportFragmentManager(), ConsentDialogFragment.TAG);
            }else{
                startActivity(intent);
                finish();
            }
        });

        String isAuthSuccess = sharedRepo.getValue(SharedRefKeys.AUTH_SUCCESSFUL);
        networkLoader.setVisibility(user==null?View.GONE:View.VISIBLE);
        mHandler.postDelayed(runnable, user==null?1500:8000);

        if(user != null && Boolean.parseBoolean(isAuthSuccess)){
            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.getRoleData().observe(this, role -> {
                if(role != null){
                    if(role.getAccessLevel() > 0){
                        intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.CONTROLLER_PAGE.name());
                        startActivity(intent);
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString(AnalyticsParam.BLOCKED_USER, "user is blocked");
                        mAnalytics.logEvent(AnalyticsParam.Event.UNEXPECTED_EVENT, bundle);
                        startActivity(blockedIntent);
                    }
                }else{
                    Log.w(TAG, "onCreate: role data is null");
                    Bundle bundle = new Bundle();
                    bundle.putString(AnalyticsParam.BAD_USER, "user is created without role data in database");
                    mAnalytics.logEvent(TAG, bundle);
                    FirebaseAuth.getInstance().signOut();
                }
                finish();
            });
        }else{
            if(user != null){
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsParam.BAD_USER, "user is created without database event");
                mAnalytics.logEvent(AnalyticsParam.Event.UNEXPECTED_EVENT, bundle);
                FirebaseAuth.getInstance().signOut();
                Log.w(TAG, "onCreate: app in bad state");
            }
        }
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsParam.BUTTON_CLICKED, "viewing privacy policy");
                mAnalytics.logEvent(AnalyticsParam.Event.VIEW_POLICY, bundle);
                webViewDialogFragment.show(getSupportFragmentManager(), "privacy_policy");
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(runnable);
        Log.w(TAG, "onDestroy: removing callback from handler");
        super.onDestroy();
    }
}