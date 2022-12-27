package com.raushankit.ILghts.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.User;
import com.raushankit.ILghts.model.room.BoardRoomData;
import com.raushankit.ILghts.response.BoardSearchResponse;
import com.raushankit.ILghts.room.BoardRepository;
import com.raushankit.ILghts.utils.callbacks.CallBack;

import java.util.Objects;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoardSearchViewModel extends AndroidViewModel {
    private static final String TAG = "BoardSearchViewModel";

    private final MutableLiveData<FilterModel> filterModelLiveData = new MutableLiveData<>();

    private final BoardRepository repository;

    public BoardSearchViewModel(@NonNull Application application) {
        super(application);
        repository = BoardRepository.getInstance(application);
    }

    public void setFilterModel(@NonNull FilterModel filterModel) {
        if(filterModel.equals(filterModelLiveData.getValue())) {
            Log.i(TAG, "setFilterModel: same filter model");
            return;
        }
        filterModelLiveData.setValue(filterModel);
    }

    public LiveData<BoardSearchResponse> getSearchResults() {
        return Transformations.switchMap(filterModelLiveData, input -> LiveDataReactiveStreams.fromPublisher(
                repository.getPublicBoards(Objects.requireNonNull(filterModelLiveData.getValue()))
                        .subscribeOn(Schedulers.io())
        ));
    }

    public void setBooleans(boolean retry, boolean nextPage) {
        FilterModel model = filterModelLiveData.getValue();
        if(model == null) { return; }
        model.setRetry(retry);
        model.setNextPage(nextPage);
        filterModelLiveData.setValue(model);
    }

    public void getAccess(BoardRoomData data, User user, int level, CallBack<String> callBack) {
        repository.requestAccess(data, user, level, callBack, true);
    }
}
