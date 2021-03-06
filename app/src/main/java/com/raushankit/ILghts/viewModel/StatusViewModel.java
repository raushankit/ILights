package com.raushankit.ILghts.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.raushankit.ILghts.observer.PulseLiveData;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatusViewModel extends ViewModel {
    private static final String TAG = "StatusViewModel";
    private static final int DELAY = 8000;
    private final PulseLiveData pulseLiveData;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    public StatusViewModel() {
        pulseLiveData = new PulseLiveData();
    }

    public LiveData<Boolean> getStatusData() {
        MediatorLiveData<Boolean> data = new MediatorLiveData<>();
        data.addSource(pulseLiveData, aLong -> {
            boolean isAlive = Calendar.getInstance().getTimeInMillis() - aLong < DELAY;
            data.setValue(isAlive);
            if (isAlive) {
                if (future != null && !future.isDone()) {
                    future.cancel(true);
                }
                future = service.schedule(() -> data.postValue(false), DELAY, TimeUnit.MILLISECONDS);
            }
        });
        return data;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        service.shutdown();
        Log.w(TAG, "onCleared: shutting down service");
    }
}
