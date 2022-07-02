package com.raushankit.ILghts.fragments.board;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.raushankit.ILghts.R;
import com.raushankit.ILghts.adapter.AdminUserAdapter;
import com.raushankit.ILghts.adapter.BoardItemAdapter;
import com.raushankit.ILghts.model.AdminUser;
import com.raushankit.ILghts.model.BoardItem;
import com.raushankit.ILghts.model.User;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment {
    public static final String TAG = "BoardFragment";
    private View view;
    private ExtendedFloatingActionButton fab;
    private boolean should_be_free = true;

    public BoardFragment() {
    }

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_board, container, false);
        fab = view.findViewById(R.id.board_fragment_fab);


        RecyclerView recyclerView = view.findViewById(R.id.board_fragment_recyclerview);
        BoardItemAdapter adapter = new BoardItemAdapter();
        recyclerView.setAdapter(adapter);
        List<BoardItem> list = new ArrayList<>();
        int i = 1;
        for(;i < 100;++i){
            list.add(new BoardItem(String.valueOf(i), "name " + i,"abc@mail.com"+i));
        }
        Log.w(TAG, "onCreateView: " + list.size());
        adapter.submitList(list);

        recyclerView.setOnScrollChangeListener((v,i1,i2,i3,i4)->{
            if(Math.abs(i4) > 25){
                if(i4 < 0 && fab.isExtended()){
                    fab.shrink();
                }
                if(i4 > 0 && !fab.isExtended()){
                    fab.extend();
                }
            }
        });

        fab.setOnClickListener(v -> {
            should_be_free = !should_be_free;
            recyclerView.setNestedScrollingEnabled(should_be_free);
        });

        return view;
    }
}