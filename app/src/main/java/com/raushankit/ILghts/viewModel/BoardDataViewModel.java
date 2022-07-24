package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.raushankit.ILghts.model.board.BoardCredModel;
import com.raushankit.ILghts.model.board.FavBoard;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.room.BoardRepository;

import java.util.List;

public class BoardDataViewModel extends AndroidViewModel {
    private static final String TAG = "BoardDataViewModel";
    private final BoardRepository mRepo;
    private final LiveData<List<BoardRoomUserData>> boardRoomUsersLivedata;
    private final MutableLiveData<String> boardIdLiveData = new MutableLiveData<>();

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

    public void getCredentials(String boardId){
        boardIdLiveData.setValue(boardId);
    }

    public LiveData<BoardCredModel> getCredentialLiveData(){
        return Transformations
                .switchMap(boardIdLiveData, (input -> LiveDataReactiveStreams
                        .fromPublisher(mRepo.getCredentialData(input)
                                .toFlowable())));
    }

    @Override
    protected void onCleared() {
        mRepo.forceCleanBoardUserList();
        super.onCleared();
        Log.w(TAG, "onCleared: ");
    }
}
