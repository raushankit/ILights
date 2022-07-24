package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.room.BoardRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoardDataViewModel extends AndroidViewModel {
    private static final String TAG = "BoardDataViewModel";
    private final BoardRepository mRepo;
    private final LiveData<List<BoardRoomUserData>> boardRoomUsersLivedata;

    public BoardDataViewModel(@NonNull Application application) {
        super(application);
        mRepo = BoardRepository.getInstance(application);
        boardRoomUsersLivedata = mRepo.getData();
    }

    public LiveData<List<BoardRoomUserData>> getData(){
        return boardRoomUsersLivedata;
    }

    public void setFavouriteBoard(@NonNull String boardId, boolean val){
        mRepo.setFavBoard(new FavBoard(boardId, val));
    }

    @Override
    protected void onCleared() {
        mRepo.forceCleanBoardUserList();
        super.onCleared();
        Log.w(TAG, "onCleared: ");
    }
}
