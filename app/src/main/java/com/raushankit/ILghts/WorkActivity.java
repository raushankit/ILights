package com.raushankit.ILghts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.raushankit.ILghts.entity.ControllerFragActions;
import com.raushankit.ILghts.entity.PageKeys;
import com.raushankit.ILghts.fragments.ControllerFragment;
import com.raushankit.ILghts.fragments.ForgotPasswordFragment;
import com.raushankit.ILghts.fragments.LoginFragment;
import com.raushankit.ILghts.fragments.SignUpFragment;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.FragViewModel;

public class WorkActivity extends AppCompatActivity {

    private static final String TAG = "WorkActivity";
    private CallBack<PageKeys> changeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        Intent signOutIntent = new Intent(this, MainActivity.class);
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
            ));
            ft.commit();
        }
        else if(id.equals(PageKeys.CONTROLLER_PAGE.name())){
            if(intent != null) intent.putExtra(PageKeys.WHICH_PAGE.name(), id);
            window.setStatusBarColor(getColor(R.color.controller_title_background));
            ft.replace(R.id.work_main_frame, ControllerFragment.newInstance());
            ft.commit();
        }else{
            Log.d(TAG, "replaceFragment: invalid Fragment action");
        }
    }
}