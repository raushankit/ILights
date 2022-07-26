package com.raushankit.ILghts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.raushankit.ILghts.adapter.BoardSearchUserAdapter;
import com.raushankit.ILghts.factory.BoardSearchViewModelFactory;
import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.viewModel.BoardSearchViewModel;

public class BoardSearchUsers extends AppCompatActivity {
    private static final String TAG = "BoardSearchUsers";

    private static final int MIN_WORDS = 4;
    private ActionMode actionMode;
    private MaterialToolbar toolbar;
    private MenuItem itemSearch;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout messageLayout;
    private RecyclerView recyclerView;
    private LottieAnimationView lottieAnimationView;
    private BoardSearchUserAdapter adapter;
    private BoardSearchViewModel viewModel;
    private Snackbar snackbar;
    private String queryStr;
    private String boardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ILights_1);
        setContentView(R.layout.activity_board_search_users);
        setBoardId();
        viewModel = new ViewModelProvider(this, new BoardSearchViewModelFactory(getApplication(), boardId))
                .get(BoardSearchViewModel.class);
        toolbar = findViewById(R.id.board_search_user_toolbar);
        shimmerFrameLayout = findViewById(R.id.board_search_user_shimmer_container);
        messageLayout = findViewById(R.id.board_search_user_error_view);
        lottieAnimationView = findViewById(R.id.board_search_user_error_lottie);
        recyclerView = findViewById(R.id.board_search_user_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Log.w(TAG, "onCreate: ");
        setSupportActionBar(toolbar);

        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_network_detected), BaseTransientBottomBar.LENGTH_LONG);
        TextView snackText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackText.setMaxLines(5);

        toolbar.setNavigationOnClickListener(view -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        });

        snackbar.setAction(R.string.retry, view -> {
            viewModel.setQuery(queryStr);
        });

        recyclerView.setVisibility(View.GONE);
        messageLayout.setVisibility(View.VISIBLE);
        adapter = new BoardSearchUserAdapter(MaterialColors.getColor(this, R.attr.flowBackgroundColor, Color.BLACK), value -> {
            if(value > 0){
                if(actionMode != null) {
                    actionMode.setTitle(value + " Selected");
                    return;
                }
                actionMode = startActionMode(actionCallback);
            }else{
                actionMode.finish();
            }
        });
        recyclerView.setAdapter(adapter);
        /*List<BoardSearchUserModel> list = new ArrayList<>();
        for(int i = 0;i < 100;++i){
            list.add(new BoardSearchUserModel(String.valueOf(i), "name " + i, "email " + i));
        }
        adapter.submitList(list);*/
        viewModel.getUsers().observe(this, list -> {
            Log.w(TAG, "onCreate: list = " + list);
            if(list.size() == 0){
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation(R.raw.no_data_found);
                lottieAnimationView.playAnimation();
            }else {
                BoardSearchUserModel item = list.get(0);
                if(item.getUserId().equals("ERROR")){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    messageLayout.setVisibility(View.GONE);
                    if(TextUtils.isEmpty(item.getName())){
                        snackText.setText(R.string.unknown_error);
                    }else{
                        snackText.setText(item.getName());
                    }
                    snackbar.show();
                }else{
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    messageLayout.setVisibility(View.GONE);
                    adapter.submitList(list);
                }
            }
        });
    }

    private void setBoardId() {
        Intent intent = getIntent();
        if(intent == null){
            finish();
        }else{
            boardId = intent.getStringExtra("BOARD_ID");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.board_search_user_menu, menu);
        itemSearch = menu.findItem(R.id.m_search);
        SearchView searchView = (SearchView) itemSearch.getActionView();
        EditText txtSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        txtSearch.setHint("Search users....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query) && query.length() < MIN_WORDS){
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.search_short_query_error, MIN_WORDS),
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                    return false;
                }
                if(query.equals(queryStr)){return false; }
                viewModel.setQuery(query);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                itemSearch.collapseActionView();
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.GONE);
                queryStr = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        itemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (itemSearch.isActionViewExpanded()) {
                    Log.w(TAG, "onMenuItemActionCollapse: ");
                    animateSearchToolbar(1, false, false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                Log.w(TAG, "onMenuItemActionExpand: ");
                animateSearchToolbar(1, true, true);
                return true;
            }
        });
        return true;
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {
        toolbar.setBackgroundColor(getColor(android.R.color.white));
        int width = toolbar.getWidth() -
                (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
        Animator createCircularReveal;
        if (show) {
            createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                    isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, 0.0f, (float) width);
            createCircularReveal.setDuration(300);
        } else {
            createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                    isRtl(getResources()) ? toolbar.getWidth() - width : width, toolbar.getHeight() / 2, (float) width, 0.0f);
            createCircularReveal.setDuration(300);
            createCircularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    toolbar.setBackgroundColor(MaterialColors.getColor(BoardSearchUsers.this, R.attr.toolbarColor, Color.WHITE));
                }
            });
        }
        createCircularReveal.start();
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private final ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater()
                    .inflate(R.menu.board_menu_items, menu);
            mode.setTitle("1 Selected ");
            setStatusBarColor(MaterialColors.getColor(BoardSearchUsers.this, R.attr.actionModeStatusBarColor, Color.WHITE));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            setStatusBarColor(MaterialColors.getColor(BoardSearchUsers.this, android.R.attr.statusBarColor, Color.WHITE));
            adapter.clearSelection();
        }
    };

    private void setStatusBarColor(@ColorInt int color){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

}