package com.raushankit.ILghts.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.raushankit.ILghts.entity.NotificationType;
import com.raushankit.ILghts.flivedata.SingleLiveData;
import com.raushankit.ILghts.model.PinData;
import com.raushankit.ILghts.model.PinInfo;
import com.raushankit.ILghts.model.PinListData;
import com.raushankit.ILghts.model.room.BoardRoomUserData;
import com.raushankit.ILghts.observer.PinInfoLivedata;
import com.raushankit.ILghts.observer.PinStatusLiveData;
import com.raushankit.ILghts.observer.PinUpdateLiveData;
import com.raushankit.ILghts.utils.StringUtils;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final SingleLiveData<String> pinMetadata;
    private final String userId;
    private final BoardRoomUserData boardData;
    private final DatabaseReference reference;

    private static final String BODY = "%s %s pin %d in board %s";


    public PinDataViewModel(Application mApplication, String userId, BoardRoomUserData boardData) {
        super(mApplication);
        this.userId = userId;
        this.boardData = boardData;
        String boardId = boardData.getBoardId();
        service = Executors.newSingleThreadExecutor();
        data = new MediatorLiveData<>();
        reference = FirebaseDatabase.getInstance().getReference();
        pinInfoLivedata = new PinInfoLivedata(boardId);
        pinStatusLiveData = new PinStatusLiveData(boardId);
        pinUpdateLiveData = new PinUpdateLiveData(boardId);
        numberLiveData = new SingleLiveData<>("/board_details/" + boardId + "/number_of_pins", Long.class);
        pinMetadata = new SingleLiveData<>("board_details/" + boardId + "/pins", String.class);
    }

    public LiveData<List<PinListData>> getPinData() {
        Log.e(TAG, "getPinData: adding observers");
        data.addSource(numberLiveData, aLong -> service.submit(this));
        data.addSource(pinMetadata, s -> service.submit(this));
        data.addSource(pinStatusLiveData, stringBooleanMap -> service.submit(this));
        data.addSource(pinUpdateLiveData, stringPinDataMap -> service.submit(this));
        data.addSource(pinInfoLivedata, stringPinInfoMap -> service.submit(this));
        return data;
    }

    public LiveData<String> getPinMetaData() {
        return pinMetadata;
    }

    @Override
    public void run() {
        List<PinListData> list = new ArrayList<>();
        Map<String, PinInfo> mpi = pinInfoLivedata.getValue();
        Map<String, PinData> mpu = pinUpdateLiveData.getValue();
        Map<String, Boolean> mps = pinStatusLiveData.getValue();
        Long num = numberLiveData.getValue();
        String pins = pinMetadata.getValue();
//        Log.e(TAG, "run: mpi = " + mpi + " mpu = " + mpu + " mps = " + mps + " num = " + num + " pins = " + pins);
        if(num != null && num == 0) {
            data.postValue(new ArrayList<>());
            return;
        }
        if(mpi == null || mps == null || mpu == null || pins == null) {
            return;
        }
        mpi.forEach((k, v) -> {
            PinData pinData = mpu.get(k);
            Boolean status = mps.get(k);
            if (pinData != null && status != null) {
                list.add(new PinListData(Integer.parseInt(k), v.getName(), v.getDescription(), pinData.getUserName(), pinData.getTimeStamp(), status, pinData.getUserUid().equals(userId)));
            }
        });
        data.postValue(list);
    }

    public void removeSources() {
        data.removeSource(numberLiveData);
        data.removeSource(pinMetadata);
        data.removeSource(pinInfoLivedata);
        data.removeSource(pinUpdateLiveData);
        data.removeSource(pinStatusLiveData);
    }

    @SuppressLint("DefaultLocale")
    public void addPin(int pin, PinInfo info, String userName, boolean isEdit, CallBack<String> callBack) {
        String boardId = boardData.getBoardId();
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("/board_control/" + boardId + "/info/" + pin, info);
        if(!isEdit) {
            mp.put("/board_control/" + boardId + "/status/" + pin, false);
            mp.put("/board_details/" + boardId + "/number_of_pins", ServerValue.increment(1));
        }
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changer_id", userId);
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changed_at", ServerValue.TIMESTAMP);
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changed_by", userName);
        String key = "user_notif/" + userId + "/" + UUID.randomUUID().toString();
        long timeStamp = StringUtils.TIMESTAMP();
        mp.put(key + "/body", String.format(BODY, "You", isEdit?"edited":"added", pin, boardData.getData().getTitle()));
        mp.put(key + "/time", -1* timeStamp);
        mp.put(key + "/type", NotificationType.TEXT);
        if(!TextUtils.equals(userId, boardData.getOwnerId())) {
            key = "user_notif/" + boardData.getOwnerId() + "/" + UUID.randomUUID().toString();
            mp.put(key + "/body", String.format(BODY, userName, isEdit?"edited":"added", pin, boardData.getData().getTitle()));
            mp.put(key + "/time", -1* timeStamp);
            mp.put(key + "/type", NotificationType.TEXT);
        }
        reference.updateChildren(mp, ((error, ref) -> {
            if(!isEdit && error == null) {
                long num = numberLiveData.getValue();
                numberLiveData.setNewValue(num + 1L);
                if(num == 0) {
                    data.removeSource(pinInfoLivedata);
                    data.removeSource(pinUpdateLiveData);
                    data.removeSource(pinStatusLiveData);
                    data.addSource(pinStatusLiveData, stringBooleanMap -> service.submit(this));
                    data.addSource(pinUpdateLiveData, stringPinDataMap -> service.submit(this));
                    data.addSource(pinInfoLivedata, stringPinInfoMap -> service.submit(this));
                }
            }
            callBack.onClick(error == null? null: error.getMessage());
        }));
    }

    public void changeState(int pin, boolean flag, String userName, CallBack<String> callBack) {
        String boardId = boardData.getBoardId();
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("/board_control/" + boardId + "/status/" + pin, flag);
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changer_id", userId);
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changed_at", ServerValue.TIMESTAMP);
        mp.put("/board_control/" + boardId + "/update/" + pin + "/changed_by", userName);
        reference.updateChildren(mp, ((error, ref) -> callBack.onClick(error == null? null: error.getMessage())));
    }

    @SuppressLint("DefaultLocale")
    public void deletePin(int pin, String userName, CallBack<String> callBack) {
        String boardId = boardData.getBoardId();
        Map<String, Object> mp = new LinkedHashMap<>();
        mp.put("/board_control/" + boardId + "/info/" + pin, null);
        mp.put("/board_control/" + boardId + "/status/" + pin, null);
        mp.put("/board_details/" + boardId + "/number_of_pins", ServerValue.increment(-1));
        mp.put("/board_control/" + boardId + "/update/" + pin, null);
        String key = "user_notif/" + userId + "/" + UUID.randomUUID().toString();
        long timeStamp = StringUtils.TIMESTAMP();
        mp.put(key + "/body", String.format(BODY, "You", "removed", pin, boardData.getData().getTitle()));
        mp.put(key + "/time", -1* timeStamp);
        mp.put(key + "/type", NotificationType.TEXT);
        if(!TextUtils.equals(userId, boardData.getOwnerId())) {
            key = "user_notif/" + boardData.getOwnerId() + "/" + UUID.randomUUID().toString();
            mp.put(key + "/body", String.format(BODY, userName, "removed", pin, boardData.getData().getTitle()));
            mp.put(key + "/time", -1* timeStamp);
            mp.put(key + "/type", NotificationType.TEXT);
        }
        reference.updateChildren(mp, ((error, ref) -> {
            if(error == null) {
                numberLiveData.setNewValue(numberLiveData.getValue() - 1L);
            }
            callBack.onClick(error == null? null: error.getMessage());
        }));
    }

    @Override
    protected void onCleared() {
        Log.i(TAG, "onCleared:");
        pinInfoLivedata.removeListeners();
        pinStatusLiveData.removeListeners();
        pinUpdateLiveData.removeListeners();
        service.shutdown();
        super.onCleared();
    }
}
