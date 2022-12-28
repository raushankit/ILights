package com.raushankit.ILghts;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.adapter.PinListAdapter;
import com.raushankit.ILghts.dialogs.AlertDialogFragment;
import com.raushankit.ILghts.dialogs.BoardControlEditFragment;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.factory.PinDataViewModelFactory;
import com.raushankit.ILghts.factory.StatusViewModelFactory;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.viewModel.PinDataViewModel;
import com.raushankit.ILghts.viewModel.PinEditDataViewModel;
import com.raushankit.ILghts.viewModel.StatusViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardControl extends AppCompatActivity {
    private static final String TAG = "BoardControl";

    private MaterialToolbar toolbar;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PinDataViewModel pinDataViewModel;
    private String pinMeta;
    private Set<Integer> pinSet = new HashSet<>();
    private boolean isEdit;
    private AtomicReference<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(StringUtils.getTheme(((BaseApp)getApplication()).getThemeIndex()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_control);

        Intent intent = getIntent();
        BoardRoomUserData boardRoomUserData = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? intent.getParcelableExtra(BoardConst.BOARD_DATA, BoardRoomUserData.class)
                : intent.getParcelableExtra(BoardConst.BOARD_DATA);
        user = new AtomicReference<>(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? intent.getParcelableExtra(BoardConst.USER, User.class)
                : intent.getParcelableExtra(BoardConst.USER));
        String userId = intent.getStringExtra(BoardConst.USER_ID);

        RelativeLayout noPinLayout = findViewById(R.id.no_switch_parent_layout);
        ProgressBar progressBar = findViewById(R.id.board_control_progress_bar);
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(R.string.confirm_action, true, true);
        toolbar = findViewById(R.id.board_control_top_appbar);
        shimmerFrameLayout = findViewById(R.id.board_control_shimmer_container);
        RecyclerView recyclerView = findViewById(R.id.board_control_list);
        MaterialButton addBoardButton = findViewById(R.id.board_control_add_switch_button);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        PinListAdapter adapter = new PinListAdapter(getString(R.string.controller_item_details), dataPair -> {
            switch (dataPair.first) {
                case EDIT:
                    isEdit = true;
                    BoardControlEditFragment.newInstance(new ArrayList<>(),
                                    dataPair.second.getPinNumber(),
                                    dataPair.second.getPinName(),
                                    dataPair.second.getPinDescription())
                            .show(getSupportFragmentManager(), BoardControlEditFragment.TAG);
                    break;
                case SWITCH:
                    pinDataViewModel.changeState(dataPair.second.getPinNumber(),
                            !dataPair.second.isStatus(),
                            user.get().getName(), err -> {
                                if(err != null) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.unknown_error,
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            });
                    break;
                case DELETE:
                    alertDialogFragment.setBodyString(getString(R.string.pin_delete_message, dataPair.second.getPinNumber()));
                    Bundle bundle = new Bundle();
                    bundle.putInt("DELETE_PIN", dataPair.second.getPinNumber());
                    alertDialogFragment.setExtraArgs(bundle);
                    alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
                    break;
                default:
                    Log.w(TAG, "onCreate: data = " + dataPair);
            }
        });
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                R.string.all_pins_active,
                BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setActionTextColor(MaterialColors.getColor(this, R.attr.colorPrimary, getColor(R.color.pure_red)));
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss());
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.board_control_menu_item_add_switch) {
                if(boardRoomUserData.getAccessLevel() <= 1) {
                    snackbar.setText(R.string.need_higher_access);
                    snackbar.setDuration(10000);
                    snackbar.show();
                    return false;
                }
                List<Integer> pinList = getAddablePins();
                if(CollectionUtils.isEmpty(pinList)) {
                    snackbar.setText(R.string.all_pins_active);
                    snackbar.setDuration(8000);
                    snackbar.show();
                    return false;
                }
                BoardControlEditFragment.newInstance(getAddablePins(), null, null, null)
                        .show(getSupportFragmentManager(), BoardControlEditFragment.TAG);
                return true;
            }
            return false;
        });
        alertDialogFragment.addWhichButtonClickedListener(whichButton -> {
            if(whichButton == AlertDialogFragment.WhichButton.POSITIVE) {
                progressBar.setVisibility(View.VISIBLE);
                pinDataViewModel.deletePin(alertDialogFragment.getExtraArgs().getInt("DELETE_PIN"),
                        user.get().getName(),
                        err -> {
                            if(err != null) {
                                Snackbar.make(findViewById(android.R.id.content),
                                        R.string.unknown_error,
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        });
            }
            alertDialogFragment.dismiss();
        });
        recyclerView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        addBoardButton.setOnClickListener(v -> {
            if(boardRoomUserData.getAccessLevel() <= 1) {
                snackbar.setText(R.string.need_higher_access);
                snackbar.setDuration(10000);
                snackbar.show();
                return;
            }
            BoardControlEditFragment.newInstance(getAddablePins(), null, null, null)
                    .show(getSupportFragmentManager(), BoardControlEditFragment.TAG);
        });
        pinDataViewModel = new ViewModelProvider(this,
                new PinDataViewModelFactory(getApplication(), userId, boardRoomUserData))
                .get(PinDataViewModel.class);
        toolbar.setTitle(boardRoomUserData.getData().getTitle());
        userViewModel.getRoleData().observe(this, role -> {
            adapter.setRole(role.getAccessLevel());
            pinDataViewModel.getPinData().observe(this, pinListData -> {
                toolbar.getMenu().findItem(R.id.board_control_menu_item_add_switch)
                        .setVisible(role.getAccessLevel() >= 1);
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                if (!pinListData.isEmpty()) {
                    noPinLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    pinListData.sort(Comparator.comparingInt(PinListData::getPinNumber));
                    adapter.submitList(pinListData);
                } else {
                    noPinLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                pinSet = pinListData.stream().map(PinListData::getPinNumber).collect(Collectors.toSet());
            });
        });
        pinDataViewModel.getPinMetaData().observe(this, s -> pinMeta = s);
        toolbar.setTitle(boardRoomUserData.getData().getTitle());
        StatusViewModel statusViewModel = new ViewModelProvider(this,
                new StatusViewModelFactory(getApplication(), boardRoomUserData.getBoardId()))
                .get(StatusViewModel.class);
        PinEditDataViewModel pinEditDataViewModel = new ViewModelProvider(this).get(PinEditDataViewModel.class);
        pinEditDataViewModel.getPinData().observe(this, data -> {
            Log.i(TAG, "onCreate: data = " + data);
            progressBar.setVisibility(View.VISIBLE);
            pinDataViewModel.addPin(data.getFirst(),
                    new PinInfo(data.component2(), data.component3()),
                    user.get().getName(), isEdit, err -> {
                        if(err != null) {
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Failed to " + (isEdit? "edit": "add") + " board",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        });
        statusViewModel.getStatusData().observe(this, aBoolean -> toolbar.setSubtitle(Boolean.TRUE.equals(aBoolean)? "Online": null));
    }

    @Override
    public Resources.Theme getTheme() {
        return super.getTheme();
    }

    private List<Integer> getAddablePins() {
        return IntStream.rangeClosed(1, pinMeta.length())
                .boxed()
                .filter(f -> pinMeta.charAt(f-1) == '1' && !pinSet.contains(f))
                .collect(Collectors.toList());

    }

    @Override
    protected void onStop() {
        pinDataViewModel.removeSources();
        super.onStop();
    }
}