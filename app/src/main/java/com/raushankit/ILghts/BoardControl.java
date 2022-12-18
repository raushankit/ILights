package com.raushankit.ILghts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.raushankit.ILghts.adapter.PinListAdapter;
import com.raushankit.ILghts.dialogs.BoardControlEditFragment;
import com.raushankit.ILghts.factory.PinDataViewModelFactory;
import com.raushankit.ILghts.factory.StatusViewModelFactory;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.viewModel.PinDataViewModel;
import com.raushankit.ILghts.viewModel.StatusViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.Comparator;

public class BoardControl extends AppCompatActivity {
    private static final String TAG = "BoardControl";

    private MaterialToolbar toolbar;

    private ShimmerFrameLayout shimmerFrameLayout;

    private PinDataViewModel pinDataViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board_control);
        RelativeLayout noPinLayout = findViewById(R.id.no_switch_parent_layout);
        toolbar = findViewById(R.id.board_control_top_appbar);
        shimmerFrameLayout = findViewById(R.id.board_control_shimmer_container);
        RecyclerView recyclerView = findViewById(R.id.board_control_list);
        MaterialButton addBoardButton = findViewById(R.id.board_control_add_switch_button);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        PinListAdapter adapter = new PinListAdapter(getString(R.string.controller_item_details), dataPair -> {
            switch (dataPair.first) {
                case EDIT:
                    BoardControlEditFragment.newInstance(12)
                            .show(getSupportFragmentManager(), BoardControlEditFragment.TAG);
                    break;
                default:
                    Log.w(TAG, "onCreate: data = " + dataPair);
            }
        });
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        addBoardButton.setOnClickListener(v -> {
            BoardControlEditFragment.newInstance(12)
                    .show(getSupportFragmentManager(), BoardControlEditFragment.TAG);
        });
        Intent intent = getIntent();
        BoardRoomUserData boardRoomUserData = intent.getParcelableExtra("BOARD");
        User user = intent.getParcelableExtra("USER");
        String userId = intent.getStringExtra("USERID");
        pinDataViewModel = new ViewModelProvider(this,
                new PinDataViewModelFactory(getApplication(), userId, boardRoomUserData.getBoardId()))
                .get(PinDataViewModel.class);
        toolbar.setTitle(boardRoomUserData.getData().getTitle());
        userViewModel.getRoleData().observe(this, role -> {
            pinDataViewModel.getPinData(userId).observe(this, pinListData -> {
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                if (!pinListData.isEmpty()) {
                    pinListData.sort(Comparator.comparingInt(PinListData::getPinNumber));
                    adapter.submitList(pinListData);
                } else {
                    noPinLayout.setVisibility(View.VISIBLE);
                }
            });
        });
        toolbar.setTitle(boardRoomUserData.getData().getTitle());
        StatusViewModel statusViewModel = new ViewModelProvider(this,
                new StatusViewModelFactory(getApplication(), boardRoomUserData.getBoardId()))
                .get(StatusViewModel.class);
        statusViewModel.getStatusData().observe(this, aBoolean -> toolbar.setSubtitle(Boolean.TRUE.equals(aBoolean)? "Online": null));
        recyclerView.setAdapter(adapter);
    }
}