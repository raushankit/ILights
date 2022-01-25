package com.raushankit.ILghts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.entity.SharedRefKeys;
import com.raushankit.ILghts.storage.SharedRepo;
import com.raushankit.ILghts.viewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        Intent intent = new Intent(this, WorkActivity.class);
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.error), true, false);
        alertDialogFragment.setPositiveButtonText(getString(R.string.exit));
        SharedRepo sharedRepo = SharedRepo.newInstance(this);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splash_screen_bg_end));
        //startActivity(new Intent(this, SettingsActivity.class));
        //finish();

        LottieAnimationView spv = findViewById(R.id.splash_screen_lottie);
        TextView spText = findViewById(R.id.splash_main_text);
        TextView versionText = findViewById(R.id.splash_screen_version_info);
        versionText.setText(getString(R.string.splash_page_version_placeholder, BuildConfig.VERSION_NAME));
        TextView helperText = findViewById(R.id.splash_screen_sign_in_btn_helper_text);

        MaterialButton registerBtn = findViewById(R.id.splash_screen_register_btn);
        MaterialButton signInBtn = findViewById(R.id.splash_screen_sign_in_btn);

        Animation zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        spText.startAnimation(zoomOutAnimation);
        spv.setMinAndMaxProgress(0.0f,0.6f);

        registerBtn.setOnClickListener(v-> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.SIGN_UP_PAGE.name());
            startActivity(intent);
            finish();
        });

        signInBtn.setOnClickListener(v-> {
            intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.LOGIN_PAGE.name());
            startActivity(intent);
            finish();
        });

        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if(whichButton == AlertDialogFragment.WhichButton.POSITIVE){
                finish();
            }
        });
        String isAuthSuccess = sharedRepo.getValue(SharedRefKeys.AUTH_SUCCESSFUL);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(()-> {
            if(user == null){
                registerBtn.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.VISIBLE);
                helperText.setVisibility(View.VISIBLE);
            }
        },getResources().getInteger(R.integer.splash_animation_time));

        if(user != null && Boolean.parseBoolean(isAuthSuccess)){
            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.getRoleData().observe(this, role -> {
                if(role != null){
                    if(role.getAccessLevel() > 0){
                        intent.putExtra(PageKeys.WHICH_PAGE.name(), PageKeys.CONTROLLER_PAGE.name());
                        startActivity(intent);
                        finish();
                    }else{
                        alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                        alertDialogFragment.setBodyString(getString(R.string.block_message));
                    }
                }
            });
        }else{
            if(user != null){
                FirebaseAuth.getInstance().signOut();
            }
        }
    }
}