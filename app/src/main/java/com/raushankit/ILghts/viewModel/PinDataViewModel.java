package com.raushankit.ILghts.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

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

public class PinDataViewModel extends ViewModel implements Runnable {
    private static final String TAG = "PinDataViewModel";
    private final MediatorLiveData<List<PinListData>> data;
    private final ExecutorService service;
    private final PinInfoLivedata pinInfoLivedata;
    private final PinUpdateLiveData pinUpdateLiveData;
    private final PinStatusLiveData pinStatusLiveData;
    private String userId;

    public PinDataViewModel() {
        service = Executors.newSingleThreadExecutor();
        data = new MediatorLiveData<>();
        pinInfoLivedata = new PinInfoLivedata();
        pinStatusLiveData = new PinStatusLiveData();
        pinUpdateLiveData = new PinUpdateLiveData();
    }

    public LiveData<List<PinListData>> getPinData(@NonNull String userId) {
        this.userId = userId;
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

        if (mpi != null && mps != null && mpu != null) {
            mpi.forEach((k, v) -> {
                PinData pinData = mpu.get(k);
                Boolean status = mps.get(k);
                if (pinData != null && status != null) {
                    list.add(new PinListData(Integer.parseInt(k), v.getName(), pinData.getUserName(), pinData.getTimeStamp(), status, pinData.getUserUid().equals(userId)));
                }
            });
        }
        data.postValue(list);
    }

    public void removeSources() {
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
