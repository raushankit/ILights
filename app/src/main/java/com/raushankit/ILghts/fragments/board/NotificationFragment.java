package com.raushankit.ILghts.fragments.board;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.NotificationAdapter;
import com.raushankit.ILghts.entity.BoardConst;
import com.raushankit.ILghts.factory.NotificationViewModelFactory;
import com.raushankit.ILghts.viewModel.NotificationViewModel;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private View view;
    private String uid;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;

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
        adapter = new NotificationAdapter(((type, data) -> {

        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.fragment_notification_recyclerview);
        shimmerFrameLayout = view.findViewById(R.id.fragment_notification_shimmer_frame);
        recyclerView.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NotificationViewModel notificationViewModel = new ViewModelProvider(requireActivity(),
                new NotificationViewModelFactory(requireActivity().getApplication(), uid))
                .get(NotificationViewModel.class);

        notificationViewModel.getFlowable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(notificationPagingData -> adapter.submitData(getLifecycle(), notificationPagingData));
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putString(BoardConst.CURRENT_FRAG, BoardConst.FRAG_NOTIFICATION);
        getParentFragmentManager()
                .setFragmentResult(BoardConst.REQUEST_KEY, args);
    }
}