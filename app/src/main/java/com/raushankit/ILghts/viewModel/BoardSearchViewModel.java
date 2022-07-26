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

public class BoardSearchViewModel extends AndroidViewModel {

    private final MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private final BoardRepository repository;
    private final String boardId;

    public BoardSearchViewModel(@NonNull Application application, @NonNull String boardId) {
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

}
