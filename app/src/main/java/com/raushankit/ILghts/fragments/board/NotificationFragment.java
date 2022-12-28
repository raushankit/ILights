package com.raushankit.ILghts.fragments.board;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.NotificationAdapter;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.factory.NotificationViewModelFactory;
import com.raushankit.ILghts.model.Notification;
import com.raushankit.ILghts.utils.callbacks.CallBack;
import com.raushankit.ILghts.viewModel.NotificationViewModel;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private String uid;
    private NotificationAdapter adapter;
    private NotificationViewModel notificationViewModel;
    private boolean action;
    private Notification actionNotification;
    private MaterialButton seenButton;
    private CallBack<String> callBack;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(@NonNull String uid) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString("user_id", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        uid = args.getString("user_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        @SuppressLint("ShowToast") Snackbar snackbar = Snackbar.make(view, R.string.error, BaseTransientBottomBar.LENGTH_LONG);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);
        adapter = new NotificationAdapter(MaterialColors.getColor(view, R.attr.flowBackgroundColor, Color.BLACK), (type, data) -> {
            switch (type) {
                case SEEN_BUTTON:
                    notificationViewModel.updateSeen(data);
                    break;
                case ACTION_ACCEPT:
                case ACTION_REJECT:
                    action = type == NotificationAdapter.WhichButton.ACTION_ACCEPT;
                    actionNotification = data;
                    notificationViewModel.doAction(actionNotification, action, callBack);
                    break;
                default:
                    Log.i(TAG, "onCreateView: unknown case: " + type);
            }
        });
        callBack = value -> {
            if(value == null) { return; }
            snackbar.setText(value).setAction(R.string.retry, v -> notificationViewModel
                    .doAction(actionNotification, action, callBack));
        };
        LinearLayout layout = view.findViewById(R.id.fragment_notification_recyclerview_parent_layout);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_notification_recyclerview);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.fragment_notification_shimmer_frame);
        seenButton = view.findViewById(R.id.fragment_notification_update_seen_button);
        layout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationViewModel = new ViewModelProvider(requireActivity(),
                new NotificationViewModelFactory(requireActivity().getApplication(), uid))
                .get(NotificationViewModel.class);

        notificationViewModel.getFlowable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(notificationPagingData -> adapter.submitData (getLifecycle(), notificationPagingData));

        notificationViewModel.countUnseen().observe(getViewLifecycleOwner(), c -> seenButton.setVisibility(c != null && c > 0? View.VISIBLE: View.GONE));

        seenButton.setOnClickListener(v -> notificationViewModel.updateAllSeen());
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_NOTIFICATION);
        args.putInt(BoardConst.CURRENT_FRAG_MENU_ID, R.id.bottom_nav_notifications);
        getParentFragmentManager()
                .setFragmentResult(BoardConst.REQUEST_KEY, args);
    }
}