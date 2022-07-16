package com.raushankit.ILghts.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.room.BoardRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoardDataViewModel extends AndroidViewModel implements Runnable{
    private final ExecutorService service;
    private final BoardRepository mRepo;
    private final MediatorLiveData<List<BoardRoomData>> data = new MediatorLiveData<>();
    private final LiveData<Map<String, Integer>> boardIdMapLivedata;
    private final LiveData<List<BoardRoomData>> boardRoomLivedata;

    public BoardDataViewModel(@NonNull Application application) {
        super(application);
        mRepo = BoardRepository.getInstance(application);
        service = Executors.newSingleThreadExecutor();
        boardIdMapLivedata = mRepo.getBoardIds();
        boardRoomLivedata = mRepo.getData();
    }

    public LiveData<List<BoardRoomData>> getBoardData(){
        data.addSource(boardIdMapLivedata, ids -> service.submit(this));
        data.addSource(boardRoomLivedata, idr -> service.submit(this));
        return data;
    }

    @Override
    public void run() {
        Map<String, Integer> boardIdMap = boardIdMapLivedata.getValue();
        List<BoardRoomData> boardRoomDataList = boardRoomLivedata.getValue();

    }

    @Override
    protected void onCleared() {
        service.shutdown();
        super.onCleared();
    }
}
