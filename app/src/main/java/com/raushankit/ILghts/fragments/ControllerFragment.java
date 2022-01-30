package com.raushankit.ILghts.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.PinListAdapter;
import com.raushankit.ILghts.entity.ControllerFragActions;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.viewModel.FragViewModel;
import com.raushankit.ILghts.viewModel.StatusViewModel;
import com.raushankit.ILghts.viewModel.UserViewModel;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ControllerFragment extends Fragment {

    private static final String TAG = "ControllerFragment";
    private FragViewModel fragViewModel;
    private ImageButton settingsButton;
    private ShimmerFrameLayout shimmerFrameLayout;
    private DatabaseReference db;
    private FirebaseAuth mAuth;

    public ControllerFragment() {
        // Required empty public constructor
    }

    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        db = FirebaseDatabase.getInstance().getReference();
        settingsButton = view.findViewById(R.id.frag_controller_settings_button);
        TextView boardStatus = view.findViewById(R.id.frag_controller_board_status);
        shimmerFrameLayout = view.findViewById(R.id.frag_controller_shimmer_container);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        AtomicReference<String> name = new AtomicReference<>();
        RecyclerView recyclerView = view.findViewById(R.id.frag_controller_pin_items_list_recyclerview);
        PinListAdapter adapter = new PinListAdapter(getString(R.string.controller_item_details),
                value -> {
                        if(name.get() != null){
                            Map<String, Object> mp = new LinkedHashMap<>();
                            mp.put("control/status/"+ value.getPinNumber(), !value.isStatus());
                            mp.put("control/update/"+ value.getPinNumber(), PinData.toMap(name.get().toLowerCase(), Objects.requireNonNull(mAuth.getUid())));
                            db.updateChildren(mp, (error, ref) -> {
                                if(error != null){
                                    Snackbar.make(view, error.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e(TAG, "onCreateView: name: " + name);
                        }
                });
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getPinData().observe(getViewLifecycleOwner(), pinListData -> {
            if(!pinListData.isEmpty()){
                pinListData.sort(Comparator.comparingInt(PinListData::getPinNumber));
                adapter.submitList(pinListData);
                if(shimmerFrameLayout.isShimmerStarted()){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            }
        });
        userViewModel.getRoleData().observe(getViewLifecycleOwner(), role -> {
            if(role.getAccessLevel() <= 0){
                fragViewModel.selectItem(ControllerFragActions.BLOCK_EVENT);
            }
        });
        StatusViewModel statusViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> name.set(user.getName()));
        statusViewModel.getStatusData().observe(getViewLifecycleOwner(), aBoolean -> boardStatus.setText(getString(R.string.controller_board_status, (aBoolean?"ONLINE":"OFFLINE"))));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragViewModel = new ViewModelProvider(requireActivity()).get(FragViewModel.class);
        fragViewModel.selectItem(ControllerFragActions.STATUS_BAR_COLOR);
        settingsButton.setOnClickListener(v -> fragViewModel.selectItem(ControllerFragActions.OPEN_SETTINGS));
    }
}