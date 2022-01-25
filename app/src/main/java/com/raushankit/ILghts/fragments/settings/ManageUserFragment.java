package com.raushankit.ILghts.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.AdminUserAdapter;
import com.raushankit.ILghts.dialogs.RoleDialogFragment;
import com.raushankit.ILghts.model.AdminUser;
import com.raushankit.ILghts.model.Role;
import com.raushankit.ILghts.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ManageUserFragment extends Fragment {

    private static final String TAG = "ManageUserFragment";
    private boolean isSearchFilterEmail;
    private View view;
    private RoleDialogFragment roleDialogFragment;
    private int accessLevel;
    private DatabaseReference db;

    public ManageUserFragment() {
        // Required empty public constructor
    }

    public static ManageUserFragment newInstance(int roleLevel) {
        ManageUserFragment fragment = new ManageUserFragment();
        Bundle args = new Bundle();
        args.putInt("role", roleLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            accessLevel = getArguments().getInt("role");
        }
        db = FirebaseDatabase.getInstance().getReference();
        roleDialogFragment = RoleDialogFragment.newInstance(accessLevel);
        roleDialogFragment.addCallback(value -> db.child("role/" + value.first).setValue(new Role(value.second))
                .addOnCompleteListener(task -> Snackbar.make(view, (task.isSuccessful()?R.string.modified_user_access:R.string.not_modified_user_access), BaseTransientBottomBar.LENGTH_SHORT).show()));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String[] searchEntries = getResources().getStringArray(R.array.admin_search_values);
        String searchFilterType = sharedPreferences.getString("admin_search", searchEntries[0]);
        isSearchFilterEmail = searchFilterType.equals(searchEntries[0]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_user, container, false);
        EditText editText = view.findViewById(R.id.manage_user_edit_text);
        FloatingActionButton searchButton = view.findViewById(R.id.manage_user_search_button);
        RecyclerView userListView = view.findViewById(R.id.manage_users_list_recyclerview);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.manage_user_shimmer_frame);
        ((SimpleItemAnimator) Objects.requireNonNull(userListView.getItemAnimator())).setSupportsChangeAnimations(false);
        userListView.addItemDecoration(new DividerItemDecoration(userListView.getContext(), DividerItemDecoration.VERTICAL));
        AdminUserAdapter adapter = new AdminUserAdapter(value -> {
            roleDialogFragment.setUser(value.getUser());
            roleDialogFragment.setUid(value.getUid());
            db.child("role/" + value.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.e(TAG, "onCreateView: role = " + task.getResult().getValue());
                            roleDialogFragment.setRole(task.getResult().getValue(Role.class));
                            roleDialogFragment.show(getChildFragmentManager(), RoleDialogFragment.TAG);
                        }else{
                            Snackbar.make(view, R.string.data_fetch_failure, BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    });
        });
        userListView.setAdapter(adapter);
        editText.setCompoundDrawablesWithIntrinsicBounds((isSearchFilterEmail?R.drawable.ic_baseline_email_24:R.drawable.ic_baseline_person_24),0,0,0);
        editText.setHint(isSearchFilterEmail?R.string.email_search_hint:R.string.name_search_hint);

        searchButton.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(editText.getText())){
                forceKeyboard(false);
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                Query query = db.child("users").orderByChild(isSearchFilterEmail?"email":"name").equalTo(editText.getText().toString().toLowerCase(Locale.ROOT));
                List<AdminUser> users = new ArrayList<>();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            users.add(new AdminUser(dataSnapshot.getKey(), dataSnapshot.getValue(User.class)));
                        }
                        if(users.isEmpty()){
                            Snackbar.make(view, getString(R.string.no_user_with_this_email, (isSearchFilterEmail?"email address":"name")), BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        adapter.submitList(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEARCH){
                if(!TextUtils.isEmpty(editText.getText())){
                    forceKeyboard(false);
                    shimmerFrameLayout.startShimmer();
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    Query query = db.child("users").orderByChild(isSearchFilterEmail?"email":"name").equalTo(editText.getText().toString().toLowerCase(Locale.ROOT));
                    List<AdminUser> users = new ArrayList<>();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                users.add(new AdminUser(dataSnapshot.getKey(), dataSnapshot.getValue(User.class)));
                            }
                            if(users.isEmpty()){
                                Snackbar.make(view, getString(R.string.no_user_with_this_email, (isSearchFilterEmail?"email address":"name")), BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                            shimmerFrameLayout.startShimmer();
                            shimmerFrameLayout.setVisibility(View.VISIBLE);
                            adapter.submitList(users);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            return false;
        });

        return view;
    }

    @SuppressWarnings("SameParameterValue")
    private void forceKeyboard(boolean toShow){
        InputMethodManager im = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(toShow){
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }else{
            im.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}