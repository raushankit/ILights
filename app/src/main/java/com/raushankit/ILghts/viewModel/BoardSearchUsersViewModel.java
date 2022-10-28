package com.raushankit.ILghts.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.raushankit.ILghts.model.board.BoardSearchUserModel;
import com.raushankit.ILghts.room.BoardRepository;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoardSearchUsersViewModel extends AndroidViewModel {

    private final MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> updateStatus = new MutableLiveData<>();
    private final BoardRepository repository;
    private Disposable disposable;
    private final String boardId;

    public BoardSearchUsersViewModel(@NonNull Application application, @NonNull String boardId) {
        super(application);
        repository = BoardRepository.getInstance(application);
        this.boardId = boardId;
    }

    public void setQuery(@NonNull String query){
        queryLiveData.setValue(query);
    }

    public LiveData<List<BoardSearchUserModel>> getUsers(){
        return Transformations
                .switchMap(queryLiveData, (input -> LiveDataReactiveStreams
                        .fromPublisher(repository.getSearchableUsersByQuery(boardId, input)
                                .toFlowable())));
    }

    public LiveData<Integer> getUpdateLiveData(){
        return updateStatus;
    }

    public void addUsers(Map<String, Object> mp){
        if(disposable != null) disposable.dispose();
        disposable = repository.addUsersToBoard(mp)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(updateStatus::postValue);
    }

    @Override
    protected void onCleared() {
        if(disposable != null) disposable.dispose();
        super.onCleared();
    }
}
