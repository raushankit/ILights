package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.raushankit.ILghts.flivedata.SingleLiveData;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.observer.PinInfoLivedata;
import com.raushankit.ILghts.observer.PinStatusLiveData;
import com.raushankit.ILghts.observer.PinUpdateLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PinDataViewModel extends AndroidViewModel implements Runnable {
    private static final String TAG = "PinDataViewModel";
    private final MediatorLiveData<List<PinListData>> data;
    private final ExecutorService service;
    private final PinInfoLivedata pinInfoLivedata;
    private final PinUpdateLiveData pinUpdateLiveData;
    private final PinStatusLiveData pinStatusLiveData;
    private final SingleLiveData<Long> numberLiveData;
    private final String userId;
    private final String boardId;


    public PinDataViewModel(Application mApplication, String userId, String boardId) {
        super(mApplication);
        this.userId = userId;
        this.boardId = boardId;
        service = Executors.newSingleThreadExecutor();
        data = new MediatorLiveData<>();
        pinInfoLivedata = new PinInfoLivedata(boardId);
        pinStatusLiveData = new PinStatusLiveData(boardId);
        pinUpdateLiveData = new PinUpdateLiveData(boardId);
        numberLiveData = new SingleLiveData<>("/board_details/" + boardId + "/number_of_pins", Long.class);
    }

    public LiveData<List<PinListData>> getPinData(@NonNull String userId) {
        data.addSource(numberLiveData, aLong -> service.submit(this));
        data.addSource(pinInfoLivedata, stringPinInfoMap -> service.submit(this));
        data.addSource(pinStatusLiveData, stringBooleanMap -> service.submit(this));
        data.addSource(pinUpdateLiveData, stringPinDataMap -> service.submit(this));
        return data;
    }

    @Override
    public void run() {
        List<PinListData> list = new ArrayList<>();
        Map<String, PinInfo> mpi = pinInfoLivedata.getValue();
        Map<String, PinData> mpu = pinUpdateLiveData.getValue();
        Map<String, Boolean> mps = pinStatusLiveData.getValue();
        Long num = numberLiveData.getValue();
        if(num != null && num == 0) {
            data.postValue(new ArrayList<>());
            return;
        }
        if(mpi == null || mps == null || mpu == null) {
            return;
        }
        mpi.forEach((k, v) -> {
            PinData pinData = mpu.get(k);
            Boolean status = mps.get(k);
            if (pinData != null && status != null) {
                list.add(new PinListData(Integer.parseInt(k), v.getName(), pinData.getUserName(), pinData.getTimeStamp(), status, pinData.getUserUid().equals(userId)));
            }
        });
        data.postValue(list);
    }

    public void removeSources() {
        data.removeSource(numberLiveData);
        data.removeSource(pinInfoLivedata);
        data.removeSource(pinUpdateLiveData);
        data.removeSource(pinStatusLiveData);
    }

    @Override
    protected void onCleared() {
        Log.w(TAG, "onCleared:");
        pinInfoLivedata.removeListeners();
        pinStatusLiveData.removeListeners();
        pinUpdateLiveData.removeListeners();
        service.shutdown();
        super.onCleared();
    }
}
