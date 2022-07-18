package com.raushankit.ILghts.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raushankit.ILghts.R;

import java.lang.ref.WeakReference;

public class BoardFabLayout {
    private static final String TAG = "BoardFabLayout";
    private final FloatingActionButton mainBtn;
    private final ExtendedFloatingActionButton newBoardBtn;
    private final ExtendedFloatingActionButton getBoardBtn;
    private boolean mainButtonClicked = false;
    private WhichButtonClickedListener listener;

    private final Animation rotateOpenAnim;
    private final Animation rotateClosedAnim;
    private final Animation fromBottomAnim;
    private final Animation toBottomAnim;

    public BoardFabLayout(final LinearLayout fabLayout, final WeakReference<Context> contextWeakReference){
        mainBtn = fabLayout.findViewById(R.id.fab_board_main);
        newBoardBtn = fabLayout.findViewById(R.id.fab_board_add_new_board);
        getBoardBtn = fabLayout.findViewById(R.id.fab_board_access_public_boards);
        newBoardBtn.setVisibility(View.GONE);
        getBoardBtn.setVisibility(View.GONE);

        rotateOpenAnim = AnimationUtils.loadAnimation(contextWeakReference.get(), R.anim.roatate_board_fab_open_animation);
        rotateClosedAnim = AnimationUtils.loadAnimation(contextWeakReference.get(), R.anim.roatate_board_fab_close_animation);
        fromBottomAnim = AnimationUtils.loadAnimation(contextWeakReference.get(), R.anim.from_board_fab_button_animation);
        toBottomAnim = AnimationUtils.loadAnimation(contextWeakReference.get(), R.anim.to_board_fab_button_animation);

        mainBtn.setOnClickListener(v -> onFabButtonCLicked());
        newBoardBtn.setOnClickListener(this::sendEvent);
        getBoardBtn.setOnClickListener(this::sendEvent);
    }

    private void onFabButtonCLicked() {
        setVisibility(mainButtonClicked);
        setAnimation(mainButtonClicked);
        mainButtonClicked = !mainButtonClicked;
        buttonSetClickable();
    }

    private void buttonSetClickable() {
        newBoardBtn.setClickable(mainButtonClicked);
        getBoardBtn.setClickable(mainButtonClicked);
    }

    private void setVisibility(boolean mainButtonClicked) {
        newBoardBtn.setVisibility(mainButtonClicked? View.GONE: View.VISIBLE);
        getBoardBtn.setVisibility(mainButtonClicked? View.GONE: View.VISIBLE);
    }

    private void setAnimation(boolean mainButtonClicked) {
        newBoardBtn.startAnimation(mainButtonClicked? toBottomAnim: fromBottomAnim);
        getBoardBtn.startAnimation(mainButtonClicked? toBottomAnim: fromBottomAnim);
        mainBtn.startAnimation(mainButtonClicked? rotateClosedAnim: rotateOpenAnim);
    }

    public void closeOptions(){
        if(!mainButtonClicked){ return; }
        onFabButtonCLicked();
    }

    public void setOnClickListener(@NonNull WhichButtonClickedListener listener){
        this.listener = listener;
    }

    private void sendEvent(View view){
        if(listener == null){ return;}
        listener.onClick(view.getId() == R.id.fab_board_add_new_board ? WhichButton.ADD_NEW_BOARD: WhichButton.GET_PUBLIC_BOARDS);
    }

    public enum WhichButton{
        ADD_NEW_BOARD, GET_PUBLIC_BOARDS
    }

    public interface WhichButtonClickedListener {
        void onClick(WhichButton whichButton);
    }

}
