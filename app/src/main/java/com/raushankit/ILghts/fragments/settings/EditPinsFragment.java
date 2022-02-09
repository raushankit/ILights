package com.raushankit.ILghts.fragments.settings;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.EditPinAdapter;
import com.raushankit.ILghts.model.EditPinInfo;
import com.raushankit.ILghts.viewModel.EditPinViewModel;
import com.raushankit.ILghts.viewModel.SettingCommViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EditPinsFragment extends Fragment {

    private static final String TAG = "EditPinsFragment";
    private SettingCommViewModel settingCommViewModel;

    public EditPinsFragment() {
        // Required empty public constructor
    }
    public static EditPinsFragment newInstance() {
        return new EditPinsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        Bundle bundle1 = new Bundle();
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_NAME, getClass().getSimpleName());
        bundle1.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Settings Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_pins, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.frag_edit_pin_list);
        FloatingActionButton fab = view.findViewById(R.id.frag_edit_pin_fab_btn);
        EditPinViewModel editPinViewModel = new ViewModelProvider(requireActivity()).get(EditPinViewModel.class);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.frag_edit_pin_shimmer_frame);

        List<Integer> pinList = new ArrayList<>();
        AtomicInteger allPin = new AtomicInteger(0);

        EditPinAdapter adapter = new EditPinAdapter(getString(R.string.add_pin_item_title), value -> settingCommViewModel.selectItem(new Pair<>("edit_pin", value)));
        recyclerView.setAdapter(adapter);

        editPinViewModel.getPinIfo().observe(getViewLifecycleOwner(), stringPinInfoMap -> {
            if(!stringPinInfoMap.isEmpty()){
                if(shimmerFrameLayout.isShimmerStarted()){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                pinList.clear();
                List<EditPinInfo> list = new ArrayList<>();
                stringPinInfoMap.forEach((k,v) -> {
                    list.add(new EditPinInfo(Integer.parseInt(k), v));
                    pinList.add(Integer.parseInt(k));
                });
                list.sort(Comparator.comparingInt(EditPinInfo::getPinNumber));
                adapter.submitList(list);
            }
        });
        editPinViewModel.getBoardData().observe(getViewLifecycleOwner(), allPin::set);
        fab.setOnClickListener(v -> {
            if(allPin.get() > 0 && !pinList.isEmpty()){
                int temp = allPin.get();
                List<Boolean> pins = new ArrayList<>();
                pins.add(Boolean.FALSE);
                while(temp > 0){
                    pins.add((temp&1)==1?Boolean.TRUE:Boolean.FALSE);
                    temp /= 2;
                }
                pinList.forEach(pin -> pins.set(pin,Boolean.FALSE));
                settingCommViewModel.selectItem(new Pair<>("add_pin", pins));
            }else{
                Log.d(TAG, "onCreateView: bad request");
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingCommViewModel = new ViewModelProvider(requireActivity()).get(SettingCommViewModel.class);
    }
}