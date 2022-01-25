package com.raushankit.ILghts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.fragments.settings.ChangeName;
import com.raushankit.ILghts.fragments.settings.EditPinItemFragment;
import com.raushankit.ILghts.fragments.settings.EditPinsFragment;
import com.raushankit.ILghts.fragments.settings.ManageUserFragment;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.VersionInfo;
import com.raushankit.ILghts.utils.ColorGen;
import com.raushankit.ILghts.utils.ProfilePic;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.SettingCommViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TAG = "SETTINGS_ACTIVITY";
    private UserViewModel userViewModel;
    private SettingCommViewModel settingCommViewModel;
    private DatabaseReference db;
    private String[] themeEntries;
    private String name;
    private AlertDialogFragment alertDialogFragment;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Intent badAuthIntent;
    private int accessLevel;
    private FirebaseAuth.AuthStateListener authListener;
    private static CallBack<Object> fragCallback;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Window window = getWindow();
        mAuth = FirebaseAuth.getInstance();
        badAuthIntent = new Intent(this, MainActivity.class);
        db = FirebaseDatabase.getInstance().getReference();
        authListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()==null){
                startActivity(badAuthIntent);
                userViewModel.resetRepository();
                finish();
            }
        };
        settingCommViewModel = new ViewModelProvider(this).get(SettingCommViewModel.class);
        alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.confirm_action),
                true,true);
        alertDialogFragment.setBodyString(getString(R.string.sign_out_body_text));
        alertDialogFragment.setPositiveButtonText(getString(R.string.yes));
        alertDialogFragment.setNegativeButtonText(getString(R.string.no));
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if(whichButton.equals(AlertDialogFragment.WhichButton.POSITIVE)){
                mAuth.signOut();
            }
            alertDialogFragment.dismiss();
        });

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splash_screen_bg_end));
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        themeEntries = getResources().getStringArray(R.array.theme_values);
        progressBar = findViewById(R.id.settings_progress_bar);
        implementListeners();
        fillUserdata();
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
    }

    private void implementListeners() {

        settingCommViewModel.getSelectedItem().observe(this, item -> {
            switch (item.first) {
                case "theme":
                    if (themeEntries[1].equals(item.second)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        getDelegate().applyDayNight();
                    }
                    if (themeEntries[2].equals(item.second)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getDelegate().applyDayNight();
                    }
                    if (themeEntries[0].equals(item.second)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        getDelegate().applyDayNight();
                    }
                    break;
                case "verify_email":
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    fragCallback.onClick(user.isEmailVerified() ? InfoType.EMAIL_VERIFIED : InfoType.EMAIL_NOT_VERIFIED);
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(task -> Snackbar.make(findViewById(android.R.id.content), (task.isSuccessful() ? getString(R.string.sent_verification_email) : getString(R.string.sent_no_verification_email)), BaseTransientBottomBar.LENGTH_SHORT).show());
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.email_already_verified), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    break;
                case "sign_out":
                    alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                    break;
                case "edit_pin":
                    if(item.second instanceof EditPinInfo){
                        final Fragment fragment = EditPinItemFragment.newInstance("edit", (EditPinInfo) item.second);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.settings_fragment, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case "add_pin":
                    List<String> list = new ArrayList<>();
                    for(int i = 1;i < ((List<?>) item.second).size();i++){
                        Boolean val = (Boolean) ((List<?>) item.second).get(i);
                        if(val) list.add(getString(R.string.add_pin_frag_item_pin_number,i));
                    }
                    if(list.isEmpty()){
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.all_pins_active),BaseTransientBottomBar.LENGTH_SHORT).show();
                    }else{
                        final Fragment fragment = EditPinItemFragment.newInstance("add", list.toArray(new String[0]));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.settings_fragment, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case "delete_pin_item":
                    int deletePinNum = (int) item.second;
                    Map<String, Object> mp1 = new LinkedHashMap<>();
                    mp1.put("control/info/"+deletePinNum,null);
                    mp1.put("control/info/"+deletePinNum, null);
                    mp1.put("control/update/"+deletePinNum, null);
                    mp1.put("control/status/"+deletePinNum, null);
                    db.updateChildren(mp1, ((error, ref) -> Snackbar.make(findViewById(android.R.id.content), (error == null?getString(R.string.pin_name_delete_successful, deletePinNum):getString(R.string.pin_name_delete_failure)), BaseTransientBottomBar.LENGTH_SHORT).show()));
                    break;
                case "edit_pin_item":
                    EditPinInfo info = (EditPinInfo) item.second;
                    db.child("control/info/"+info.getPinNumber()).setValue(info.getPinInfo())
                            .addOnCompleteListener(task -> Snackbar.make(findViewById(android.R.id.content), (task.isSuccessful()?getString(R.string.pin_name_update_successful, info.getPinNumber()):getString(R.string.pin_name_update_failure)), BaseTransientBottomBar.LENGTH_SHORT).show());
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                case "add_pin_item":
                    EditPinInfo info1 = (EditPinInfo) item.second;
                    Map<String, Object> mp = new LinkedHashMap<>();
                    mp.put("control/info/"+info1.getPinNumber(), info1.getPinInfo());
                    mp.put("control/update/"+info1.getPinNumber(), new PinData(mAuth.getUid(), Calendar.getInstance().getTimeInMillis(),name));
                    mp.put("control/status/"+info1.getPinNumber(), Boolean.FALSE);
                    db.updateChildren(mp, ((error, ref) -> Snackbar.make(findViewById(android.R.id.content), (error == null?getString(R.string.pin_name_add_successful, info1.getPinNumber()):getString(R.string.pin_name_add_failure)), BaseTransientBottomBar.LENGTH_SHORT).show()));
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
            }
        });
    }

    private void fillUserdata() {
        ConstraintLayout userLayout = findViewById(R.id.settings_user_data);
        ProfilePic pic = userLayout.findViewById(R.id.user_info_profile_pic);
        TextView nameView = userLayout.findViewById(R.id.user_info_name);
        TextView emailView = userLayout.findViewById(R.id.user_info_email);
        TextView roleView = userLayout.findViewById(R.id.user_info_access_level);
        String []roleType = getResources().getStringArray(R.array.user_types);

        pic.setBackGroundCol(ColorGen.getRandomDarkColor(0.7f));
        boolean[] flag = new boolean[2];

        userViewModel.getUserData().observe(this, user -> {
            if(user != null){
                name = user.getName();
                if(!flag[0]){
                    flag[0] = true;
                    if(flag[1]){
                        dataReceiveEvent();
                    }
                }
                nameView.setText(user.getName());
                pic.setName(user.getName());
                emailView.setText(user.getEmail());
            }else{
                startActivity(badAuthIntent);
                finish();
            }
        });
        userViewModel.getRoleData().observe(this, role -> {
            if(!flag[1]){
                flag[1] = true;
                if(flag[0]){
                    dataReceiveEvent();
                }
            }
            if(role != null){
                accessLevel = Math.max(0,Math.min(roleType.length, role.getAccessLevel()));
                if(accessLevel < 1){
                    startActivity(badAuthIntent);
                    finish();
                }
                fragCallback.onClick(accessLevel >= 2?InfoType.ADMIN_VISIBILITY:InfoType.ADMIN_INVISIBILITY);
                roleView.setText(getString(R.string.access_level_place_holder, roleType[accessLevel]));
            }else{
                startActivity(badAuthIntent);
                finish();
            }
        });

        userViewModel.getVersionData().observe(this, versionInfo -> {
            if(versionInfo != null) fragCallback.onClick(versionInfo);
        });
    }

    private void dataReceiveEvent() {
        progressBar.setVisibility(View.GONE);
        if(fragCallback != null){
            fragCallback.onClick(InfoType.PROFILE_VISIBILITY);
            fragCallback.onClick(Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()?InfoType.EMAIL_VERIFIED:InfoType.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        if(pref.getKey().equals("change_name")){
            args.putString("prev_name", name);
            args.putString("user_uid", mAuth.getUid());
            pref.setFragment(ChangeName.class.getName());
        }
        if(pref.getKey().equals("manage_users")){
            args.putInt("role", accessLevel);
            pref.setFragment(ManageUserFragment.class.getName());
        }
        if(pref.getKey().equals("manage_pins")){
            args.putString("user_name", name);
            args.putString("user_uid", mAuth.getUid());
            pref.setFragment(EditPinsFragment.class.getName());
        }
        final Fragment fragment = getSupportFragmentManager()
                .getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        getSupportFragmentManager().setFragmentResultListener("request", this, (requestKey, result) -> {
            if(requestKey.equals("request")){
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
        mAuth.addAuthStateListener(authListener);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAuth.removeAuthStateListener(authListener);
        super.onDestroy();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        private SettingCommViewModel settingCommViewModelFrag;

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            settingCommViewModelFrag = new ViewModelProvider(requireActivity()).get(SettingCommViewModel.class);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference themePreference = findPreference("theme");
            PreferenceCategory profileCategory = findPreference("profile_category");
            PreferenceCategory adminCategory = findPreference("admin_category");
            Preference verifyEmailPreference = findPreference("verify_email");
            Preference versionNamePreference = findPreference("version_name");
            Preference updatePreference = findPreference("update_available");
            Preference signOutPreference = findPreference("sign_out");

            fragCallback = value -> {
                if(value instanceof InfoType){
                    if(value.equals(InfoType.PROFILE_VISIBILITY)){
                        if(profileCategory != null) profileCategory.setEnabled(true);
                    }
                    else if(value.equals(InfoType.EMAIL_NOT_VERIFIED)){
                        if(verifyEmailPreference != null) verifyEmailPreference.setSummary(R.string.email_not_verified);
                    }
                    else if(value.equals(InfoType.EMAIL_VERIFIED)){
                        if(verifyEmailPreference != null) verifyEmailPreference.setSummary(R.string.email_verified);
                    }
                    else if(value.equals(InfoType.ADMIN_VISIBILITY) || value.equals(InfoType.ADMIN_INVISIBILITY)){
                        if(adminCategory != null) adminCategory.setEnabled(value.equals(InfoType.ADMIN_VISIBILITY));
                    }
                }else if(value instanceof VersionInfo){
                    if(updatePreference != null){
                        updatePreference.setVisible(BuildConfig.VERSION_CODE < ((VersionInfo) value).getVersionCode());
                        updatePreference.setSummary(getString(R.string.update_summary, BuildConfig.VERSION_NAME, ((VersionInfo) value).getVersionName()));
                    }
                }
            };

            if(themePreference != null) themePreference.setOnPreferenceChangeListener(this);
            if(verifyEmailPreference != null) verifyEmailPreference.setOnPreferenceClickListener(this);
            if(signOutPreference != null) signOutPreference.setOnPreferenceClickListener(this);
            if(versionNamePreference != null) versionNamePreference.setSummary(BuildConfig.VERSION_NAME);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(preference.getKey().equals("theme")){
                settingCommViewModelFrag.selectItem(new Pair<>("theme",newValue));
                return true;
            }
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equals("verify_email")){
                settingCommViewModelFrag.selectItem(new Pair<>("verify_email",null));
                return true;
            }else if(preference.getKey().equals("sign_out")){
                settingCommViewModelFrag.selectItem(new Pair<>("sign_out",null));
                return true;
            }else{
                Log.d(TAG, "onPreferenceClick: unknown click event");
            }
            return false;
        }
    }

    public enum InfoType{
        PROFILE_VISIBILITY,
        EMAIL_VERIFIED,
        EMAIL_NOT_VERIFIED,
        ADMIN_VISIBILITY,
        ADMIN_INVISIBILITY
    }
}