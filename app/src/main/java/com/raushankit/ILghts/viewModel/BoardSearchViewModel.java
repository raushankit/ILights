package com.raushankit.ILghts.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.raushankit.ILghts.model.FilterModel;
import com.raushankit.ILghts.model.board.BoardSearchModel;

import java.util.List;

public class BoardSearchViewModel extends AndroidViewModel {

    private final MutableLiveData<FilterModel> filterModelLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<BoardSearchModel>> data = new MutableLiveData<>();

    public BoardSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<FilterModel> getFilterModel() {
        return filterModelLiveData;
    }

    public void setFilterModel(@Nullable FilterModel filterModel) {
        filterModelLiveData.setValue(filterModel);
    }

    public LiveData<List<BoardSearchModel>> getSearchResults() {
        return Transformations
                .switchMap(filterModelLiveData, input -> {
                    return null;
                });
    }

}
